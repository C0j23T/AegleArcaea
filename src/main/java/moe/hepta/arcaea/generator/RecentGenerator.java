package moe.hepta.arcaea.generator;

import moe.hepta.arcaea.Arcaea;
import moe.hepta.arcaea.utils.ArcaeaHelper;
import moe.hepta.arcaea.Resources;
import moe.hepta.arcaea.beans.UserInfo;
import moe.hepta.graphics.IGraphicsModel;
import moe.hepta.graphics.Panel;
import moe.hepta.graphics.model.ImageModel;
import moe.hepta.graphics.model.TextOnlyModel;
import moe.hepta.arcaea.model.PotentialModel;
import moe.hepta.arcaea.model.RoundImageModel;

import java.awt.*;
import java.io.IOException;

public class RecentGenerator {
    public static byte[] generate(UserInfo recent) throws IOException {
        Arcaea.instance.logger.info("Got the song grade, generating recent");
        boolean isAwakened = recent.getContent().getAccountInfo().getIsCharUncapped() && !recent.getContent().getAccountInfo().getIsCharUncappedOverride();
        long start = System.currentTimeMillis();
        Panel panel = ArcaeaHelper.getRecentTemplate();
        String code = recent.getContent().getAccountInfo().getCode();
        int space = 0;
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = code.length() - 1; i >= 0; i--) {
            space++;
            codeBuilder.append(code.charAt(i));
            if (space == 3) {
                codeBuilder.append(" ");
                space = 0;
            }
        }
        code = codeBuilder.reverse().substring(1);
        panel.draw(new IGraphicsModel[]{
                new ImageModel(ArcaeaHelper.partnerIcon(recent.getContent().getAccountInfo().getCharacter(), isAwakened), 185, 70, 169, 170),
                new PotentialModel(recent.getContent().getAccountInfo().getRating(), 264, 147, 133),
                new TextOnlyModel(recent.getContent().getAccountInfo().getName(), Resources.GeoSansLight55, Color.WHITE, 420, 115),
                new TextOnlyModel(code, Resources.GeoSansLight40, Color.WHITE, 420, 185),
                new RoundImageModel(ArcaeaHelper.songImage(recent.getContent().getRecentScore().get(0).getSongId(), recent.getContent().getRecentScore().get(0).getDifficulty()), 185, 380, 512, 512, 40),
                new ImageModel(Resources.difficultyTags[Math.min(3, recent.getContent().getRecentScore().get(0).getDifficulty())], 827, 414, 148, 43),
                new TextOnlyModel(String.format("%.1f", ArcaeaHelper.getSongRating(recent.getContent().getRecentScore().get(0).getSongId(), recent.getContent().getRecentScore().get(0).getDifficulty())), Resources.Exo48, Color.WHITE, 1020, 404),
                new TextOnlyModel(String.format("%.1f", recent.getContent().getRecentScore().get(0).getRating()), Resources.Exo48, Color.WHITE, 1314, 404),
                new TextOnlyModel(ArcaeaHelper.shortWidth(ArcaeaHelper.songName(recent.getContent().getRecentScore().get(0).getSongId(), recent.getContent().getRecentScore().get(0).getDifficulty()), 800, Resources.SourceHanCJK96, panel), Resources.SourceHanCJK96, Color.WHITE, 827, 480),
                new TextOnlyModel(ArcaeaHelper.shortWidth(ArcaeaHelper.getSongComposer(recent.getContent().getRecentScore().get(0).getSongId(), recent.getContent().getRecentScore().get(0).getDifficulty()), 740, Resources.SourceHanCJK64, panel), Resources.SourceHanCJK64, Color.WHITE.darker(), 827, 615),
                new TextOnlyModel(ArcaeaHelper.parseScore(recent.getContent().getRecentScore().get(0).getScore()), Resources.GeoSansLight96, Color.WHITE, 827, 780),
                new ImageModel(Resources.grade[ArcaeaHelper.scoreRate(recent.getContent().getRecentScore().get(0).getScore())], 1310, 760, 150),
                new TextOnlyModel(String.format("%s(+%s)", recent.getContent().getRecentScore().get(0).getPerfectCount(), recent.getContent().getRecentScore().get(0).getShinyPerfectCount()), Resources.Exo38, Color.WHITE.darker(), 1755, 687),
                new TextOnlyModel(String.valueOf(recent.getContent().getRecentScore().get(0).getNearCount()), Resources.Exo38, Color.WHITE.darker(), 1700, 752),
                new TextOnlyModel(String.valueOf(recent.getContent().getRecentScore().get(0).getMissCount()), Resources.Exo38, Color.WHITE.darker(), 1700, 817)
        });
        Arcaea.instance.logger.info("Generation complete, took {} ms", System.currentTimeMillis() - start);
        return panel.outputBytes(Arcaea.instance.imageQuality);
    }
}
