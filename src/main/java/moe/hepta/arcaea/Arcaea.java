package moe.hepta.arcaea;

import com.google.gson.Gson;
import moe.aegle.command.CommandManager;
import moe.aegle.command.beans.CommandObject;
import moe.aegle.command.enums.Permission;
import moe.aegle.module.Module;
import moe.hepta.arcaea.beans.RawSongList;
import moe.hepta.arcaea.utils.AUAAccessor;
import moe.hepta.arcaea.command.CommandArcaea;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Arcaea extends Module {
    public static Arcaea instance;
    public File configFile, userAccountFile, songInfoFile;
    public boolean configurator = false;
    public Logger logger = getLogger();
    public Properties api = new Properties();
    public Map<String, RawSongList.ContentDTO.SongsDTO> songInfo;
    public Map<Long, Integer> userAccount;
    public String apiAddress, APIToken;
    public int imageQuality = 0;
    public static final Gson gson = new Gson();

    @Override
    public void onDisable() {
        logger.info("正在关闭Arcaea查分模块");
        saveAccount();
    }

    @Override
    public void onLoad() {
        instance = this;
        if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
            boolean ignored = getDataFolder().mkdir();
        }
        configFile = new File(getDataFolder(), File.separator + "config.properties");
        if (!configFile.exists() || configFile.isDirectory()) {
            logger.warn("配置文件未找到，这可能是第一次启动，将不会生效");
            logger.warn("将会生成配置文件，填好后重启模块即可");
            try {
                boolean ignored = configFile.createNewFile();
                try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8)) {
                    api.setProperty("apiAddress", "");
                    api.setProperty("apiToken", "");
                    api.store(writer, String.valueOf(System.currentTimeMillis()));
                }
            } catch (IOException e) {
                logger.error("Error while creating config file", e);
            }
            return;
        }
        configurator = true;
        songInfoFile = new File(getDataFolder(), File.separator + "songInfo.object");
        userAccountFile = new File(getDataFolder(), File.separator + "userAccount.object");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        if (!configurator) {
            logger.warn("模块未被正确配置，将不会生效");
            return;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
            api.load(reader);
        } catch (IOException e) {
            logger.error("Error while loading config", e);
        }
        apiAddress = api.getProperty("apiAddress");
        if (!apiAddress.endsWith("/")) apiAddress = apiAddress + "/";
        APIToken = api.getProperty("apiToken");
        if (apiAddress.trim().length() == 0 || APIToken.trim().length() == 0) {
            logger.warn("模块未被正确配置，将不会生效");
            return;
        }
        if (!songInfoFile.exists() || songInfoFile.isDirectory() || songInfoFile.length() < 1024) {
            logger.warn("曲目文件未找到，将通过API获取曲目信息");
            try {
                String jsonSongInfo = AUAAccessor.requestString("song/list");
                var rawSongInfo = gson.fromJson(jsonSongInfo, RawSongList.class);
                songInfo = new HashMap<>();
                for (var info : rawSongInfo.getContent().getSongs()) {
                    songInfo.put(info.getSongId(), info);
                }
                if (saveSongInfo()) logger.info("曲目文件获取成功");
            } catch (Exception e) {
                logger.error("在获取曲目信息时发生错误（有可能是因为HTTP状态码不为200导致的）", e);
            }
        } else {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(songInfoFile))) {
                songInfo = (HashMap<String, RawSongList.ContentDTO.SongsDTO>) input.readObject();
                logger.info("曲目文件加载成功");
            } catch (FileNotFoundException e) {
                logger.error("Cannot load song info file", e);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (userAccountFile.exists() && !userAccountFile.isDirectory() && userAccountFile.length() != 0) {
            try {
                boolean ignored = userAccountFile.createNewFile();
                try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(userAccountFile))) {
                    userAccount = (HashMap<Long, Integer>) input.readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                logger.error("Cannot load user account file", e);
            }
        } else userAccount = new HashMap<>();
        Resources.init();
        CommandManager.getInstance().registerCommand("arcaea", new CommandObject(
                Permission.MEMBER,
                new CommandArcaea(),
                false,
                false,
                false,
                false,
                "Arcaea模块主要命令",
                new String[]{"arc", "a"}
        ), this);
        logger.info("Arcaea模块已加载");
    }

    public boolean saveSongInfo() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(songInfoFile))) {
            output.writeObject(songInfo);
            return true;
        } catch (FileNotFoundException e) {
            logger.error("Cannot save song info file", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void saveAccount() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(userAccountFile))) {
            output.writeObject(userAccount);
            logger.info("用户信息已保存");
        } catch (FileNotFoundException e) {
            logger.error("Cannot save user account file", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}