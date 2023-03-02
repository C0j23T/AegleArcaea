package moe.hepta.arcaea.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.aegle.command.CommandExecutor;
import moe.aegle.command.beans.CommandMessage;
import moe.aegle.command.enums.Permission;
import moe.hepta.arcaea.Arcaea;
import moe.hepta.arcaea.beans.B30Bean;
import moe.hepta.arcaea.beans.RawSongList;
import moe.hepta.arcaea.beans.UserInfo;
import moe.hepta.arcaea.generator.B30Generator;
import moe.hepta.arcaea.generator.RecentGenerator;
import moe.hepta.arcaea.utils.AUAAccessor;
import moe.hepta.arcaea.utils.ArcaeaHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.*;

public class CommandArcaea implements CommandExecutor {
    private static final String helpMessage = """
                ----闊靛緥婧愮偣 指令帮助----
                arcaea bind <用户名或用户代码> - 绑定一个闊靛緥婧愮偣账户
                arcaea unbind - 解除闊靛緥婧愮偣绑定
                arcaea - 查询最近一次游玩成绩
                arcaea info <歌名:可选> - 查询账户最近一次游玩或曲目最佳成绩
                arcaea b30 - 查询账户b30
                arcaea alias <歌名> - 查询曲目别名
                arcaea chart <歌名> <难度> - 查看曲目谱面预览
                arcaea update - 更新曲目列表
                添加别名请联系Bot所有者""";

    @Override
    public String onCommand(CommandMessage commandMessage) {
        if (commandMessage.getArgs().length == 1) {
            if (!Arcaea.instance.userAccount.containsKey(commandMessage.getSender().senderID())) {
                commandMessage.getOperator().sendMessage(commandMessage, "你还没有绑定Arcaea账户", false, Arcaea.instance);
                return null;
            }
            try {
                Map<String, String> params = new HashMap<>();
                params.put("user", String.valueOf(Arcaea.instance.userAccount.get(commandMessage.getSender().senderID())));
                commandMessage.getOperator().sendMessage(commandMessage, "Recent请求已收到，正在查询中，这可能需要点时间...", false, Arcaea.instance);
                String rawJson = AUAAccessor.requestStringWithParams("user/info", params);
                if (rawJson == null) {
                    commandMessage.getOperator().sendMessage(commandMessage, "获取失败，请稍后重试", false, Arcaea.instance);
                }
                var userInfo = Arcaea.gson.fromJson(rawJson, UserInfo.class);
                if (userInfo.getStatus() != 0) {
                    commandMessage.getOperator().sendMessage(commandMessage, "发生错误：" + processCode(userInfo.getStatus()), false, Arcaea.instance);
                    return null;
                }
                byte[] output = RecentGenerator.generate(userInfo);
                commandMessage.getOperator().sendMessage(commandMessage, "[CQ:image,file=base64://" + Base64.encodeBase64String(output) + "]", false, Arcaea.instance);
            } catch (IOException e) {
                commandMessage.getOperator().sendMessage(commandMessage, "无法获取recent，可能是服务器超时或是内部错误，请稍后重试", false, Arcaea.instance);
                Arcaea.instance.logger.error("Could not generate recent", e);
            }
            return null;
        }
        switch (commandMessage.getArgs()[1]) {
            case "bind" -> {
                if (commandMessage.getArgs().length != 3) {
                    commandMessage.getOperator().sendMessage(commandMessage, "参数错误，应为arcaea bind <用户名称/代码>", false, Arcaea.instance);
                    return null;
                }
                if (Arcaea.instance.userAccount.containsKey(commandMessage.getSender().senderID())) {
                    commandMessage.getOperator().sendMessage(commandMessage, "你已绑定了一个Arcaea账户", false, Arcaea.instance);
                    return null;
                }
                Map<String, String> params = new HashMap<>();
                params.put("user", commandMessage.getArgs()[2]);
                try {
                    String json = Validate.notNull(AUAAccessor.requestStringWithParams("user/info", params));
                    JsonObject object = JsonParser.parseString(json).getAsJsonObject();
                    if (object.get("status").getAsShort() == 0) {
                        int userCode = object.get("content").getAsJsonObject().get("account_info").getAsJsonObject().get("code").getAsInt();
                        Arcaea.instance.userAccount.put(commandMessage.getSender().senderID(), userCode);
                        commandMessage.getOperator().sendMessage(commandMessage, "绑定成功：" + object.get("content").getAsJsonObject().get("account_info").getAsJsonObject().get("name").getAsString() + "(" + userCode + ")", false, Arcaea.instance);
                        Arcaea.instance.saveAccount();
                    } else {
                        commandMessage.getOperator().sendMessage(commandMessage, "发生错误：" + processCode(object.get("status").getAsShort()), false, Arcaea.instance);
                        return null;
                    }
                } catch (Exception e) {
                    commandMessage.getOperator().sendMessage(commandMessage, "阿勒，服务器没有正确回答呢，确认参数正确或者等会再试吧", false, Arcaea.instance);
                    return null;
                }
            }
            case "unbind" -> {
                if (!Arcaea.instance.userAccount.containsKey(commandMessage.getSender().senderID())) {
                    commandMessage.getOperator().sendMessage(commandMessage, "你还没有绑定Arcaea账户", false, Arcaea.instance);
                    return null;
                }
                Arcaea.instance.userAccount.remove(commandMessage.getSender().senderID());
                commandMessage.getOperator().sendMessage(commandMessage, "账户解绑成功", false, Arcaea.instance);
                Arcaea.instance.saveAccount();
            }
            case "quality" -> {
                if (commandMessage.getSender().senderPermission().compareTo(Permission.MANAGER) < 0) return null;
                if (commandMessage.getArgs().length != 3) {
                    commandMessage.getOperator().sendMessage(commandMessage, "参数错误，应为arcaea quality <图片质量>\n0 - 全分辨率\n1 - 三分之二分辨率\n2 - 二分之一分辨率", false, Arcaea.instance);
                    return null;
                }
                switch (commandMessage.getArgs()[2]) {
                    case "0" -> {
                        Arcaea.instance.imageQuality = 0;
                        commandMessage.getOperator().sendMessage(commandMessage, "全局图片分辨率已调整为全分辨率", false, Arcaea.instance);
                    }
                    case "1" -> {
                        Arcaea.instance.imageQuality = 1;
                        commandMessage.getOperator().sendMessage(commandMessage, "全局图片分辨率已调整为三分之二分辨率", false, Arcaea.instance);
                    }
                    case "2" -> {
                        Arcaea.instance.imageQuality = 2;
                        commandMessage.getOperator().sendMessage(commandMessage, "全局图片分辨率已调整为二分之一分辨率", false, Arcaea.instance);
                    }
                    default -> {
                        commandMessage.getOperator().sendMessage(commandMessage, "参数错误，应为arcaea quality <图片质量>\n0 - 全分辨率\n1 - 三分之二分辨率\n2 - 二分之一分辨率", false, Arcaea.instance);
                        return null;
                    }
                }
            }
            case "b30" -> {
                if (!Arcaea.instance.userAccount.containsKey(commandMessage.getSender().senderID())) {
                    commandMessage.getOperator().sendMessage(commandMessage, "你还没有绑定Arcaea账户", false, Arcaea.instance);
                    return null;
                }
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("user", String.valueOf(Arcaea.instance.userAccount.get(commandMessage.getSender().senderID())));
                    params.put("overflow", "3");
                    commandMessage.getOperator().sendMessage(commandMessage, "Bests请求已收到，正在查询中，这可能需要点时间...", false, Arcaea.instance);
                    String rawJson = AUAAccessor.requestStringWithParams("user/best30", params);var b30 = Arcaea.gson.fromJson(rawJson, B30Bean.class);
                    if (b30 == null) {
                        commandMessage.getOperator().sendMessage(commandMessage, "获取失败，请稍后重试", false, Arcaea.instance);
                        return null;
                    }
                    if (b30.getStatus() != 0) {
                        commandMessage.getOperator().sendMessage(commandMessage, "发生错误：" + processCode(b30.getStatus()), false, Arcaea.instance);
                        return null;
                    }
                    byte[] output = B30Generator.generate(b30);
                    commandMessage.getOperator().sendMessage(commandMessage, "[CQ:image,file=base64://" + Base64.encodeBase64String(output) + "]", false, Arcaea.instance);
                } catch (IOException e) {
                    commandMessage.getOperator().sendMessage(commandMessage, "无法获取b30，可能是服务器超时或是内部错误，请稍后重试", false, Arcaea.instance);
                    Arcaea.instance.logger.error("Could not generate b30", e);
                }
            }
            case "update" -> {
                if (commandMessage.getSender().senderPermission().compareTo(Permission.MANAGER) <= 0) return null;
                try {
                    String jsonSongInfo = AUAAccessor.requestString("song/list");
                    var rawSongInfo = Arcaea.gson.fromJson(jsonSongInfo, RawSongList.class);
                    Map<String, List<String>> aliases = new HashMap<>();
                    for (var info : Arcaea.instance.songInfo.entrySet()) {
                        aliases.put(info.getKey(), info.getValue().getAlias());
                    }
                    Arcaea.instance.songInfo = new HashMap<>();
                    for (var info : rawSongInfo.getContent().getSongs()) {
                        Arcaea.instance.songInfo.put(info.getSongId(), info);
                        Set<String> addAliases = new HashSet<>(info.getAlias());
                        if (aliases.containsKey(info.getSongId())) addAliases.addAll(aliases.get(info.getSongId()));
                        Arcaea.instance.songInfo.get(info.getSongId()).setAlias(new ArrayList<>(addAliases));
                    }
                    if (Arcaea.instance.saveSongInfo()) {
                        commandMessage.getOperator().sendMessage(commandMessage, "曲目列表更新成功", false, Arcaea.instance);
                        Arcaea.instance.logger.info("曲目文件获取成功");
                    }
                } catch (Exception e) {
                    commandMessage.getOperator().sendMessage(commandMessage, "曲目列表更新失败", false, Arcaea.instance);
                    Arcaea.instance.logger.error("在获取曲目信息时发生错误（有可能是因为HTTP状态码不为200导致的）", e);
                }
            }
            case "info" -> {
                if (!Arcaea.instance.userAccount.containsKey(commandMessage.getSender().senderID())) {
                    commandMessage.getOperator().sendMessage(commandMessage, "你还没有绑定Arcaea账户", false, Arcaea.instance);
                    return null;
                }
                try {
                    Map<String, String> params = new HashMap<>();
                    params.put("user", String.valueOf(Arcaea.instance.userAccount.get(commandMessage.getSender().senderID())));
                    if (commandMessage.getArgs().length != 2) {
                        String difficulty = "2";
                        if (commandMessage.getArgs().length == 4) {
                            difficulty = ArcaeaHelper.difficultyAbbreviationConverter(commandMessage.getArgs()[3]);
                            if (difficulty.equals("-1")) {
                                commandMessage.getOperator().sendMessage(commandMessage, "没有这种难度呢，换个方式再试试吧", false, Arcaea.instance);
                                return null;
                            }
                        }
                        String songId = ArcaeaHelper.getSongId(commandMessage.getArgs()[2], Integer.parseInt(difficulty));
                        if (songId.equals("-1")) {
                            commandMessage.getOperator().sendMessage(commandMessage, "没有叫这个名字的曲目呢，换个方式再试试吧", false, Arcaea.instance);
                            return null;
                        }
                        if (!ArcaeaHelper.haveDifficulty(songId, Integer.parseInt(difficulty))) {
                            commandMessage.getOperator().sendMessage(commandMessage, "这个曲目没有这个难度", false, Arcaea.instance);
                            return null;
                        }
                        params.put("difficulty", difficulty);
                        params.put("songid", songId);
                    }
                    commandMessage.getOperator().sendMessage(commandMessage, "Recent/Best请求已收到，正在查询中，这可能需要点时间...", false, Arcaea.instance);
                    String rawJson = AUAAccessor.requestStringWithParams(commandMessage.getArgs().length == 2 ? "user/info" : "user/best", params);
                    if (rawJson == null) {
                        commandMessage.getOperator().sendMessage(commandMessage, "获取失败或者尚未游玩该难度的曲目，请稍后重试", false, Arcaea.instance);
                    }
                    var userInfo = Arcaea.gson.fromJson(rawJson, UserInfo.class);
                    if (userInfo.getStatus() != 0) {
                        commandMessage.getOperator().sendMessage(commandMessage, "发生错误：" + processCode(userInfo.getStatus()), false, Arcaea.instance);
                        return null;
                    }
                    if (userInfo.getContent().getRecord() != null)
                        userInfo.getContent().setRecentScore(List.of(userInfo.getContent().getRecord()));
                    byte[] output = RecentGenerator.generate(userInfo);
                    commandMessage.getOperator().sendMessage(commandMessage, "[CQ:image,file=base64://" + Base64.encodeBase64String(output) + "]", false, Arcaea.instance);
                } catch (IOException e) {
                    commandMessage.getOperator().sendMessage(commandMessage, "无法获取recent/best，可能是服务器超时或是内部错误，请稍后重试", false, Arcaea.instance);
                    Arcaea.instance.logger.error("Could not generate recent", e);
                }
            }
            case "chart" -> {
                if (commandMessage.getArgs().length != 4) {
                    commandMessage.getOperator().sendMessage(commandMessage, "参数错误，应为arcaea chart <歌名> <难度>", false, Arcaea.instance);
                }
                String difficulty = ArcaeaHelper.difficultyAbbreviationConverter(commandMessage.getArgs()[3]);
                if (difficulty.equals("-1")) {
                    commandMessage.getOperator().sendMessage(commandMessage, "没有这种难度呢，换个方式再试试吧", false, Arcaea.instance);
                    return null;
                }
                String songId = ArcaeaHelper.getSongId(commandMessage.getArgs()[2], Integer.parseInt(difficulty));
                if (songId.equals("-1")) {
                    commandMessage.getOperator().sendMessage(commandMessage, "没有叫这个名字的曲目呢，换个方式再试试吧", false, Arcaea.instance);
                    return null;
                }
                if (!ArcaeaHelper.haveDifficulty(songId, Integer.parseInt(difficulty))) {
                    commandMessage.getOperator().sendMessage(commandMessage, "这个曲目没有这个难度", false, Arcaea.instance);
                    return null;
                }
                byte[] output = ArcaeaHelper.getChartPreview(songId, difficulty);
                commandMessage.getOperator().sendMessage(commandMessage, "[CQ:image,file=base64://" + Base64.encodeBase64String(output) + "]", false, Arcaea.instance);
            }
            case "alias" -> {
                if (commandMessage.getArgs().length < 3) {
                    commandMessage.getOperator().sendMessage(commandMessage, "参数错误，应为arcaea alias <歌名>", false, Arcaea.instance);
                    return null;
                }
                if (commandMessage.getSender().senderPermission().compareTo(Permission.MANAGER) >= 0 && commandMessage.getArgs().length == 5) {
                    String songId = ArcaeaHelper.getSongId(commandMessage.getArgs()[3], 0);
                    if (songId.equals("-1")) {
                        commandMessage.getOperator().sendMessage(commandMessage, "没有叫这个名字的曲目呢，换个方式再试试吧", false, Arcaea.instance);
                        return null;
                    }
                    if (commandMessage.getArgs()[2].equalsIgnoreCase("add")) {
                        var aliases = Arcaea.instance.songInfo.get(songId).getAlias();
                        if (!aliases.contains(commandMessage.getArgs()[4])) aliases.add(commandMessage.getArgs()[4]);
                        commandMessage.getOperator().sendMessage(commandMessage, String.format("%s 的别名 %s 添加成功", ArcaeaHelper.songName(songId, 0), commandMessage.getArgs()[4]), false, Arcaea.instance);
                    }
                    if (commandMessage.getArgs()[2].equalsIgnoreCase("remove")) {
                        Arcaea.instance.songInfo.get(songId).getAlias().removeIf(s -> s.equalsIgnoreCase(commandMessage.getArgs()[4]));
                        commandMessage.getOperator().sendMessage(commandMessage, "删除成功", false, Arcaea.instance);
                    }
                    return null;
                }
                String songId = ArcaeaHelper.getSongId(commandMessage.getArgs()[2], 0);
                if (songId.equals("-1")) {
                    commandMessage.getOperator().sendMessage(commandMessage, "没有叫这个名字的曲目呢，换个方式再试试吧", false, Arcaea.instance);
                    return null;
                }
                int page = (commandMessage.getArgs().length == 4 &&StringUtils.isNumeric(commandMessage.getArgs()[3])) ? Integer.parseInt(commandMessage.getArgs()[3]) : 0;
                int listStart = page * 10, listEnd = (page + 1) * 10 - 1;
                StringBuilder sb = new StringBuilder(ArcaeaHelper.songName(songId, 0));
                sb.append(" 的别名有：\n");
                var aliases = Arcaea.instance.songInfo.get(songId).getAlias();
                for (int i = listStart; i <= listEnd; i++) {
                    if (i + 1 > aliases.size()) break;
                    sb.append(i)
                            .append(". ")
                            .append(aliases.get(i))
                            .append("\n");
                }
                sb.append("-----第 ").append(page).append("/").append(aliases.size() / 10).append("页-----\n");
                sb.append("使用arcaea alias \"")
                        .append(commandMessage.getArgs()[2])
                        .append("\" <页数> 跳转到目标页数");
                commandMessage.getOperator().sendMessage(commandMessage, sb.toString(), false, Arcaea.instance);
            }
            default -> {
                commandMessage.getOperator().sendMessage(commandMessage, helpMessage, false, Arcaea.instance);
                return null;
            }
        }
        return null;
    }

    private static String processCode(int code) {
        String result = "请求错误(" + code + ")";
        switch (code) {
            case -1 -> result = "无效的用户名或用户代码(-1)";
            case -2 -> result = "无效的用户代码(-2)";
            case -3 -> result = "无此用户(-3)";
            case -4 -> result = "太多匹配的用户(-4)";
            case -13 -> result = "添加好友失败(-13)";
            case -15 -> result = "尚未游玩该曲目(-15)";
            case -14 -> result = "曲目不存在此谱面(-14)";
            case -16 -> result = "用户排行榜被封禁(-16)";
            case -23 -> result = "用户潜力值过低(≤7.00)，无法使用该功能(-23)";
            case -24 -> result = "Arcaea版本更新，请通知所有者更新(-24)";
        }
        return result;
    }
}
