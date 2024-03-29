package moe.hepta.arcaea.utils;

import moe.hepta.arcaea.Arcaea;
import moe.hepta.arcaea.Resources;
import moe.hepta.graphics.Panel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ArcaeaHelper {
    public static File mkdir(String... paths) {
        StringBuilder builder = new StringBuilder();
        File directory = null;
        for (String s : paths) {
            builder.append(s);
            directory = new File(Arcaea.instance.getDataFolder(), builder.toString());
            if (!directory.isDirectory()) {
                boolean ignore = directory.mkdirs();
            }
            builder.append(File.separator);
        }
        return directory;
    }
    public static BufferedImage ratingImage(short potential) {
        short image = 8;
        if (potential >= 0) image = 0;
        if (potential >= 350) image = 1;
        if (potential >= 700) image = 2;
        if (potential >= 1000) image = 3;
        if (potential >= 1100) image = 4;
        if (potential >= 1200) image = 5;
        if (potential >= 1250) image = 6;
        if (potential >= 1300) image = 7;

        return Resources.rating[image];
    }

    public static BufferedImage partnerIcon(int partner, boolean awakened) throws IOException {
        File f = new File(mkdir("resources", "icons"), File.separator + partner + "_" + awakened + ".png");
        if (!f.exists()) {
            Arcaea.instance.logger.warn("The icon of the partner was not found, downloading");
            Map<String, String> params = new HashMap<>();
            params.put("partner", String.valueOf(partner));
            params.put("awakened", String.valueOf(awakened));
            try {
                byte[] image = AUAAccessor.requestBytesWithParams("assets/icon", params);
                String imageString = ImageBase64Utils.bytes2Base64(image);
                ImageBase64Utils.base64ToImage(imageString, f);
            } catch (IOException e) {
                Arcaea.instance.logger.error("Could not download the icon of the partner {}", partner, e);
            }
        }
        return ImageIO.read(f);
    }

    public static BufferedImage partnerImage(int partner, boolean awakened) throws IOException {
        File f = new File(mkdir("resources", "partner"), File.separator + partner + "_" + awakened + ".png");
        if (!f.exists()) {
            Arcaea.instance.logger.warn("The partner image was not found, downloading");
            Map<String, String> params = new HashMap<>();
            params.put("partner", String.valueOf(partner));
            params.put("awakened", String.valueOf(awakened));
            try {
                byte[] image = AUAAccessor.requestBytesWithParams("assets/char", params);
                String imageString = ImageBase64Utils.bytes2Base64(image);
                ImageBase64Utils.base64ToImage(imageString, f);
            } catch (IOException e) {
                Arcaea.instance.logger.error("Could not download the image of the partner {}", partner, e);
            }
        }
        return ImageIO.read(f);
    }

    public static BufferedImage songImage(String songId, int difficulty) throws IOException {
        if (songId.equals("lasteternity")) difficulty = 3;
        File f = new File(mkdir("resources", "song"), File.separator + songId + "_" + difficulty + ".png");
        if (!f.exists()) {
            Arcaea.instance.logger.warn("The song image was not found, downloading");
            Map<String, String> params = new HashMap<>();
            params.put("songid", songId);
            params.put("difficulty", String.valueOf(difficulty));
            try {
                byte[] image = AUAAccessor.requestBytesWithParams("assets/song", params);
                String imageString = ImageBase64Utils.bytes2Base64(image);
                ImageBase64Utils.base64ToImage(imageString, f);
            } catch (IOException e) {
                Arcaea.instance.logger.error("Could not download the image of the song {}", songId, e);
            }
        }
        return ImageIO.read(f);
    }

    public static boolean haveDifficulty(String songId, int difficulty) {
        return Arcaea.instance.songInfo.get(songId).getDifficulties().size() >= difficulty + 1;
    }

    public static BufferedImage getSongBG(String songId, int difficulty) {
        String bgFileName = Arcaea.instance.songInfo.get(songId).getDifficulties().get(difficulty).getBg() + ".jpg";
        if (".jpg".equals(bgFileName)) {
            int side = Arcaea.instance.songInfo.get(songId).getDifficulties().get(difficulty).getSide();
            if (side == 0) bgFileName = "single_light.jpg";
            else bgFileName = "single_conflict.jpg";
        }
        File bgFile = new File(mkdir("resources", "songBg"), File.separator + bgFileName);
        if (!bgFile.exists()) {
            Arcaea.instance.logger.error("没有对应的背景文件，请执行*a update");
            return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
        }
        try {
            return ImageIO.read(bgFile);
        } catch (IOException e) {
            Arcaea.instance.logger.error("", e);
        }
        return new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
    }
    public static byte[] getChartPreview(String songId, String difficulty) {
        File f = new File(mkdir("resources", "preview"), File.separator + songId + "_" + difficulty + ".png");
        if (!f.exists()) {
            Arcaea.instance.logger.warn("The chart preview was not found, downloading");
            Map<String, String> params = new HashMap<>();
            params.put("songid", songId);
            params.put("difficulty", difficulty);
            try {
                byte[] image = AUAAccessor.requestBytesWithParams("assets/preview", params);
                String imageString = ImageBase64Utils.bytes2Base64(image);
                ImageBase64Utils.base64ToImage(imageString, f);
            } catch (IOException e) {
                Arcaea.instance.logger.error("Could not download the image of the song {}", songId, e);
            }
        }
        try (FileInputStream fileInputStream = new FileInputStream(f)) {
            byte[] result = new byte[(int) f.length()];
            IOUtils.readFully(fileInputStream, result);
            return result;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static Color difficultyColor(int i) {
        if (i == 3) return new Color(165, 20, 49);
        if (i == 2) return new Color(115, 35, 110);
        if (i == 1) return new Color(120, 155, 80);
        if (i == 0) return new Color(20, 165, 215);
        return new Color(255, 255, 255);
    }

    public static int scoreRate(int score) {
        int result = 6;
        if (score >= 8600000) result = 5;
        if (score >= 8900000) result = 4;
        if (score >= 9200000) result = 3;
        if (score >= 9500000) result = 2;
        if (score >= 9800000) result = 1;
        if (score >= 9900000) result = 0;
        return result;
    }

    public static String parseScore(int score) {
        String scoreStr = String.valueOf(score);
        StringBuilder result = new StringBuilder();
        int len = scoreStr.length();
        for (int i = 0; i < 8; i++) {
            int j = len - 8 + i;
            result.append(j < 0 ? '0' : scoreStr.charAt(j));
            if (i == 4 || i == 1) result.append("'");
        }
        return result.toString();
    }

    public static String songName(String songId, int difficulty) {
        return Arcaea.instance.songInfo.get(songId).getDifficulties().get(difficulty).getNameEn();
    }

    private static final Random random = new Random();

    public static moe.hepta.graphics.Panel getRecentTemplate() {
        int r = random.nextInt(23);
        return Panel.copy(Resources.recentTemplates[r]);
    }

    public static float getSongRating(String songId, int difficulty) {
        return Arcaea.instance.songInfo.get(songId).getDifficulties().get(difficulty).getRating() / 10.0f;
    }

    public static String getSongComposer(String songId, int difficulty) {
        return Arcaea.instance.songInfo.get(songId).getDifficulties().get(difficulty).getArtist();
    }

    public static String shortWidth(String src, int width, Font font, Panel panel) {
        if (src == null || src.length() == 0) return "";
        FontMetrics fm = panel.GraphicsFromBackGround().getFontMetrics(font);
        String result = src;
        for (int j = 20; j > 5; j--) {
            int sw = fm.stringWidth(result);
            if (sw > width) {
                result = StringUtils.abbreviate(src, j);
            } else break;
        }
        return result;
    }

    public static int textWidth(String src,  Font font, Panel panel) {
        if (src == null) return 0;
        FontMetrics fm = panel.GraphicsFromBackGround().getFontMetrics(font);
        return fm.stringWidth(src);
    }

    private static final List<String> beyond = List.of("beyond", "byd", "byn", "3"),
            future = List.of("future", "ftr", "2"),
            present = List.of("present", "prs", "1"),
            past = List.of("past", "pst", "0");

    public static String difficultyAbbreviationConverter(@NotNull String input) {
        if (beyond.contains(input.toLowerCase())) return "3";
        if (future.contains(input.toLowerCase())) return "2";
        if (present.contains(input.toLowerCase())) return "1";
        if (past.contains(input.toLowerCase())) return "0";
        return "-1";
    }

    public static String getSongId(String raw, int difficulty) {
        for (var info : Arcaea.instance.songInfo.entrySet()) {
            if (info.getValue().getDifficulties().size() <= difficulty) continue;

            if (info.getKey().equalsIgnoreCase(raw)) return info.getKey();
            if (info.getValue().getDifficulties().get(difficulty).getNameEn().equalsIgnoreCase(raw)) return info.getKey();
            if (info.getValue().getDifficulties().get(difficulty).getNameJp().equalsIgnoreCase(raw)) return info.getKey();
            for (var alias : info.getValue().getAlias()) {
                if (alias.equalsIgnoreCase(raw)) return info.getKey();
            }
        }
        return "-1";
    }
}
