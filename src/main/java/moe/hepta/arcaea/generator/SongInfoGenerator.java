package moe.hepta.arcaea.generator;

import com.jhlabs.image.GaussianFilter;
import moe.aegle.scheduler.ScheduleManager;
import moe.hepta.arcaea.Arcaea;
import moe.hepta.arcaea.Resources;
import moe.hepta.arcaea.beans.RawSongList;
import moe.hepta.arcaea.utils.ArcaeaHelper;
import moe.hepta.graphics.Panel;
import moe.hepta.graphics.model.ImageModel;
import moe.hepta.graphics.model.TextOnlyModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.*;

public class SongInfoGenerator {
    public static byte[] generate(RawSongList.ContentDTO.SongsDTO song) throws IOException {
        File file = new File(ArcaeaHelper.mkdir("resources", "songInfo"), song.getSongId() + ".jpg");
        if (file.exists()) {
            return IOUtils.toByteArray(new FileInputStream(file));
        }
        Arcaea.instance.logger.info("Generating songInfo for " + song.getSongId());
        long start = System.currentTimeMillis();
        GaussianFilter gaussianFilter = new GaussianFilter(20);
        Panel panel = new Panel(2560, 1440);
        int constAverage = 0;
        for (var tmp : song.getDifficulties()) {
            constAverage = constAverage + tmp.getRating();
        }
        constAverage = constAverage / song.getDifficulties().size();
        panel.draw(new ImageModel(Resources.songInfoBg, 0, (int) (constAverage / 130.0 * -2656), 2560, 4096));
        panel.draw(new ImageModel(gaussianFilter.filter(ArcaeaHelper.getSongBG(song.getSongId(), 0), null), -295, 0, 1281, 960));
        panel.draw(new ImageModel(Resources.songInfoTemplate, 0, 0, 2560, 1440));
        panel.draw(new ImageModel(ArcaeaHelper.songImage(song.getSongId(), 0), 363, 379, 512, 512));

        String tmp = song.getDifficulties().get(0).getSetFriendly();
        panel.draw(new TextOnlyModel(ArcaeaHelper.shortWidth(tmp, 650, Resources.GeoSansLight96, panel), Resources.GeoSansLight96, Color.WHITE, 39, 18));
        tmp = new String(song.getDifficulties().get(0).getNameEn().getBytes()).toUpperCase();
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(tmp), 831, Resources.SourceHanCJK64, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK64, Color.WHITE, 875 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK64, panel), 911));
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(0).getArtist()), 410, Resources.SourceHanCJK48, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK48, Color.WHITE, 875 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK48, panel), 1025));
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(0).getJacketDesigner()), 410, Resources.SourceHanCJK48, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK48, Color.WHITE, 875 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK48, panel), 1110));
        tmp = String.valueOf(song.getDifficulties().get(0).getTime()) + 's';
        panel.draw(new TextOnlyModel(tmp, Resources.SourceHanCJK48, Color.WHITE, 875 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK48, panel), 1200));

        panel.draw(new TextOnlyModel("Past " + song.getDifficulties().get(0).getRating() / 10.0f, Resources.GeoSansLight65, Color.WHITE, 1580, 448));
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(0).getChartDesigner()), 400, Resources.SourceHanCJK36, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK36, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK36, panel), 533));
        panel.draw(new TextOnlyModel("Notes: " + song.getDifficulties().get(0).getNote(), Resources.Exo38, Color.WHITE, 1500, 606));
        tmp = "MaxPTT: " + (song.getDifficulties().get(0).getRating() / 10.0f + 2);
        panel.draw(new TextOnlyModel(tmp, Resources.Exo38, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.Exo38, panel), 606));

        panel.draw(new TextOnlyModel("Present " + song.getDifficulties().get(1).getRating() / 10.0f, Resources.GeoSansLight65, Color.WHITE, 1580, 672));
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(1).getChartDesigner()), 400, Resources.SourceHanCJK36, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK36, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK36, panel), 774));
        panel.draw(new TextOnlyModel("Notes: " + song.getDifficulties().get(1).getNote(), Resources.Exo38, Color.WHITE, 1500, 828));
        tmp = "MaxPTT: " + (song.getDifficulties().get(1).getRating() / 10.0f + 2);
        panel.draw(new TextOnlyModel(tmp, Resources.Exo38, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.Exo38, panel), 828));

        panel.draw(new TextOnlyModel("Future " + song.getDifficulties().get(2).getRating() / 10.0f, Resources.GeoSansLight65, Color.WHITE, 1580, 892));
        tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(2).getChartDesigner()), 400, Resources.SourceHanCJK36, panel);
        panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK36, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK36, panel), 985));
        panel.draw(new TextOnlyModel("Notes: " + song.getDifficulties().get(2).getNote(), Resources.Exo38, Color.WHITE, 1500, 1050));
        tmp = "MaxPTT: " + (song.getDifficulties().get(2).getRating() / 10.0f + 2);
        panel.draw(new TextOnlyModel(tmp, Resources.Exo38, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.Exo38, panel), 1050));

        if (song.getDifficulties().size() > 3) {
            panel.draw(new ImageModel(Resources.songInfoBydBg, 1499, 1114, 577, 206));
            panel.draw(new TextOnlyModel("Beyond " + song.getDifficulties().get(3).getRating() / 10.0f, Resources.GeoSansLight65, Color.WHITE, 1580, 1114));
            tmp = ArcaeaHelper.shortWidth(StringUtils.reverse(song.getDifficulties().get(3).getChartDesigner()), 400, Resources.SourceHanCJK36, panel);
            panel.draw(new TextOnlyModel(StringUtils.reverse(tmp), Resources.SourceHanCJK36, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.SourceHanCJK36, panel), 1203));
            panel.draw(new TextOnlyModel("Notes: " + song.getDifficulties().get(3).getNote(), Resources.Exo38, Color.WHITE, 1500, 1272));
            tmp = "MaxPTT: " + (song.getDifficulties().get(3).getRating() / 10.0f + 2);
            panel.draw(new TextOnlyModel(tmp, Resources.Exo38, Color.WHITE, 2076 - ArcaeaHelper.textWidth(tmp, Resources.Exo38, panel), 1272));
        }
        Arcaea.instance.logger.info("Generation complete, took {} ms", System.currentTimeMillis() - start);
        byte[] output = panel.outputBytes(Arcaea.instance.imageQuality);
        ScheduleManager.getInstance().runTaskLaterAsynchronously(Arcaea.instance, () -> {
            byte[] bytes = new byte[output.length];
            System.arraycopy(output, 0, bytes, 0, bytes.length);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (int i = 0; i < bytes.length; i++) {
                    if (bytes[i] < 0) bytes[i] += 256;
                }
                fos.write(bytes);
                fos.flush();
            } catch (IOException e) {
                Arcaea.instance.logger.error("Cannot save songInfo", e);
            }
        }, 0);
        return output;
    }
}
