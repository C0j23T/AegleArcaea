package moe.hepta.arcaea.generator;

import moe.hepta.arcaea.Arcaea;
import moe.hepta.arcaea.Resources;
import moe.hepta.arcaea.model.RoundImageModel;
import moe.hepta.arcaea.model.RoundRectangleModel;
import moe.hepta.arcaea.utils.ArcaeaHelper;
import moe.hepta.arcaea.beans.B30Bean;
import moe.hepta.graphics.IGraphicsModel;
import moe.hepta.graphics.Panel;
import moe.hepta.graphics.model.ImageModel;
import moe.hepta.graphics.model.TextOnlyModel;
import moe.hepta.arcaea.model.PotentialModel;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;

public class B30Generator {
    private static final Color pureColor = new Color(120, 114, 205),
            farColor = new Color(181, 186, 0),
            lostColor = Color.BLACK;

    public static byte[] generate(B30Bean b30) throws IOException {
        Arcaea.instance.logger.info("Got the b30 info, generating banner");
        boolean isAwakened = b30.getContent().getAccountInfo().getIsCharUncapped() && !b30.getContent().getAccountInfo().getIsCharUncappedOverride();
        long start = System.currentTimeMillis();
        Panel panel = Panel.copy(Resources.b30Template);
        panel.draw(new IGraphicsModel[]{
                new ImageModel(ArcaeaHelper.partnerIcon(b30.getContent().getAccountInfo().getCharacter(), isAwakened), 199, 516, 169, 170),
                new PotentialModel(b30.getContent().getAccountInfo().getRating(), 275, 593, 133),
                new TextOnlyModel(StringUtils.abbreviate(b30.getContent().getAccountInfo().getName(), 15), Resources.GeoSansLight55, Color.WHITE, 400, 555),
                new TextOnlyModel(b30.getContent().getAccountInfo().getCode(), Resources.GeoSansLight34Compact, Color.WHITE, 400, 633),
                new ImageModel(ArcaeaHelper.partnerImage(b30.getContent().getAccountInfo().getCharacter(), isAwakened), 830, 10, 1200, 1200),
                new ImageModel(Resources.b30piece, -19, 82, 1082, 703),
                new TextOnlyModel(String.format("%.4f", b30.getContent().getRecent10Avg()), Resources.GeoSansLight73, Color.WHITE, 1075, 508),
                new TextOnlyModel(String.format("%.4f", b30.getContent().getBest30Avg()), Resources.GeoSansLight73, Color.WHITE, 1075, 677)

        });
        Arcaea.instance.logger.info("Generating b30");
        for (var i = 0; i < b30.getContent().getBest30List().size(); i++) {
            int x = 128 + i % 3 * 598, y = 910 + i / 3 * 375;
            float playTime = (System.currentTimeMillis() - b30.getContent().getBest30List().get(i).getTimePlayed()) / 3600000.0f;
            String formattedPlayTime = a(panel, x, y, playTime);
            panel.draw(new TextOnlyModel("#" + (i + 1), Resources.Exo24, Color.BLACK, x + 31, y + 36));
            panel.draw(new RoundRectangleModel(ArcaeaHelper.difficultyColor(b30.getContent().getBest30List().get(i).getDifficulty()), x + 82, y + 31, 105, 40, 40));
            String rating = String.format("%.2f", b30.getContent().getBest30List().get(i).getRating());
            FontMetrics fm = panel.GraphicsFromBackGround().getFontMetrics(Resources.Exo24);
            int sw = fm.stringWidth(rating);
            panel.draw(new IGraphicsModel[]{
                    new TextOnlyModel(rating, Resources.Exo24, Color.WHITE, x + 135 - sw / 2, y + 36),
                    new ImageModel(Resources.grade[ArcaeaHelper.scoreRate(b30.getContent().getBest30List().get(i).getScore())], x + 180, y + 1, 100),
                    new TextOnlyModel(ArcaeaHelper.shortWidth(ArcaeaHelper.songName(b30.getContent().getBest30List().get(i).getSongId(), b30.getContent().getBest30List().get(i).getDifficulty()), 260, Resources.SourceHanCJK46, panel), Resources.SourceHanCJK46, Color.BLACK, x + 270, y + 15),
                    new RoundImageModel(ArcaeaHelper.songImage(b30.getContent().getBest30List().get(i).getSongId(), b30.getContent().getBest30List().get(i).getDifficulty()), x + 34, y + 95, 153, 153, 500),
                    new TextOnlyModel(ArcaeaHelper.parseScore(b30.getContent().getBest30List().get(i).getScore()), Resources.GeoSansLight65, Color.BLACK, x + 205, y + 100),
                    new TextOnlyModel(String.format("PURE %s (+%s)", b30.getContent().getBest30List().get(i).getPerfectCount(), b30.getContent().getBest30List().get(i).getShinyPerfectCount()), Resources.Exo26, pureColor, x + 205, y + 170),
                    new TextOnlyModel("FAR " + b30.getContent().getBest30List().get(i).getNearCount(), Resources.Exo26, farColor, x + 205, y + 205),
                    new TextOnlyModel("LOST " + b30.getContent().getBest30List().get(i).getMissCount(), Resources.Exo26, lostColor, x + 205, y + 240),
                    new TextOnlyModel(formattedPlayTime, Resources.GeoSansLight34, Color.BLACK, x + 400, y + 240)
            });
        }
        Arcaea.instance.logger.info("Generating overflow");
        for (int i = 0; i < Math.min(b30.getContent().getBest30Overflow().size(), 3); i++) {
            int x = 128 + i % 3 * 598, y = 4748;
            float playTime = (System.currentTimeMillis() - b30.getContent().getBest30Overflow().get(i).getTimePlayed()) / 3600000.0f;
            String formattedPlayTime = a(panel, x, y, playTime);
            panel.draw(new TextOnlyModel("#" + (i + 31), Resources.Exo24, Color.BLACK, x + 31, y + 36));
            panel.draw((new RoundRectangleModel(ArcaeaHelper.difficultyColor(b30.getContent().getBest30Overflow().get(i).getDifficulty()), x + 82, y + 31, 105, 40, 40)));
            String rating = String.format("%.2f", b30.getContent().getBest30Overflow().get(i).getRating());
            FontMetrics fm = panel.GraphicsFromBackGround().getFontMetrics(Resources.Exo24);
            int sw = fm.stringWidth(rating);
            panel.draw(new IGraphicsModel[]{
                    new TextOnlyModel(rating, Resources.Exo24, Color.WHITE, x + 135 - sw / 2, y + 36),
                    new ImageModel(Resources.grade[ArcaeaHelper.scoreRate(b30.getContent().getBest30Overflow().get(i).getScore())], x + 180, y + 1, 100),
                    new TextOnlyModel(ArcaeaHelper.shortWidth(ArcaeaHelper.songName(b30.getContent().getBest30Overflow().get(i).getSongId(), b30.getContent().getBest30Overflow().get(i).getDifficulty()), 260, Resources.SourceHanCJK46, panel), Resources.SourceHanCJK46, Color.BLACK, x + 270, y + 15),
                    new RoundImageModel(ArcaeaHelper.songImage(b30.getContent().getBest30Overflow().get(i).getSongId(), b30.getContent().getBest30Overflow().get(i).getDifficulty()), x + 34, y + 95, 153, 153, 500),
                    new TextOnlyModel(ArcaeaHelper.parseScore(b30.getContent().getBest30Overflow().get(i).getScore()), Resources.GeoSansLight65, Color.BLACK, x + 205, y + 100),
                    new TextOnlyModel(String.format("PURE %s (+%s)", b30.getContent().getBest30Overflow().get(i).getPerfectCount(), b30.getContent().getBest30Overflow().get(i).getShinyPerfectCount()), Resources.Exo26, pureColor, x + 205, y + 170),
                    new TextOnlyModel("FAR " + b30.getContent().getBest30Overflow().get(i).getNearCount(), Resources.Exo26, farColor, x + 205, y + 205),
                    new TextOnlyModel("LOST " + b30.getContent().getBest30Overflow().get(i).getMissCount(), Resources.Exo26, lostColor, x + 205, y + 240),
                    new TextOnlyModel(formattedPlayTime, Resources.GeoSansLight34, Color.BLACK, x + 400, y + 240)
            });
        }
        Arcaea.instance.logger.info("Generation complete, took {} ms", System.currentTimeMillis() - start);
        return panel.outputBytes(Arcaea.instance.imageQuality);
    }

    private static String a(Panel panel, int x, int y, float playTime) {
        String formattedPlayTime;
        if (playTime >= 24) {
            playTime = playTime / 24.0f;
            formattedPlayTime = String.format("%.2fD", playTime);
        } else formattedPlayTime = String.format("%.2fH", playTime);

        panel.draw(new RoundRectangleModel(Color.WHITE, x, y, 549, 306, 50));
        return formattedPlayTime;
    }
}
