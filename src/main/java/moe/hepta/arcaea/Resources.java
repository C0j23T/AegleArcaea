package moe.hepta.arcaea;

import com.jhlabs.image.GaussianFilter;
import moe.hepta.graphics.Panel;
import moe.hepta.graphics.model.ImageModel;
import moe.hepta.arcaea.utils.ArcaeaHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Resources {
    public static BufferedImage[] difficultyTags, grade, rating, recentTemplates = new BufferedImage[23];
    public static BufferedImage b30Template, recentTemplate, b30piece, songInfoBg, songInfoTemplate, songInfoBydBg;
    public static Font Exo54, Exo48, Exo38, Exo24, Exo26,
            GeoSansLight96, GeoSansLight55, GeoSansLight40, GeoSansLight34Compact, GeoSansLight34, GeoSansLight73, GeoSansLight65,
            SourceHanCJK46, SourceHanCJK64, SourceHanCJK96, SourceHanCJK48, SourceHanCJK36;

    public static void init() {
        try {
            Arcaea.instance.logger.info("Reading difficult tags");
            difficultyTags = new BufferedImage[]{
                    ImageIO.read(Arcaea.instance.getResource("difficultyTag/tag-difficulty-past.png")),
                    ImageIO.read(Arcaea.instance.getResource("difficultyTag/tag-difficulty-present.png")),
                    ImageIO.read(Arcaea.instance.getResource("difficultyTag/tag-difficulty-future.png")),
                    ImageIO.read(Arcaea.instance.getResource("difficultyTag/tag-difficulty-beyond.png"))
            };
            Arcaea.instance.logger.info("Reading grade tags");
            grade = new BufferedImage[]{
                    ImageIO.read(Arcaea.instance.getResource("grade/0.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/1.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/2.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/3.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/4.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/5.png")),
                    ImageIO.read(Arcaea.instance.getResource("grade/6.png"))
            };
            Arcaea.instance.logger.info("Reading ratings");
            rating = new BufferedImage[]{
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_0.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_1.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_2.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_3.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_4.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_5.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_6.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_7.png")),
                    ImageIO.read(Arcaea.instance.getResource("rating/rating_off.png"))
            };
            Arcaea.instance.logger.info("Reading templates");
            b30Template = ImageIO.read(Arcaea.instance.getResource("b30_template.png"));
            b30piece = ImageIO.read(Arcaea.instance.getResource("b30_piece1.png"));
            recentTemplate = ImageIO.read(Arcaea.instance.getResource("recent_template.png"));
            songInfoBg = ImageIO.read(Arcaea.instance.getResource("songInfo/bg_small.png"));
            songInfoTemplate = ImageIO.read(Arcaea.instance.getResource("songInfo_template.png"));
            songInfoBydBg = ImageIO.read(Arcaea.instance.getResource("songInfo/byd.png"));
            Arcaea.instance.logger.info("Reading fonts");
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            Exo54 = loadFont(Arcaea.instance.getResource("fonts/Exo-Regular.ttf"), 54).deriveFont(attributes);
            Exo38 = loadFont(Arcaea.instance.getResource("fonts/Exo-Regular.ttf"), 38).deriveFont(attributes);
            Exo24 = loadFont(Arcaea.instance.getResource("fonts/Exo-Regular.ttf"), 24);
            Exo26 = loadFont(Arcaea.instance.getResource("fonts/Exo-Regular.ttf"), 26);
            Exo48 = loadFont(Arcaea.instance.getResource("fonts/Exo-Regular.ttf"), 48);
            GeoSansLight55 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 55).deriveFont(attributes);
            GeoSansLight34Compact = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 34).deriveFont(attributes);
            GeoSansLight34 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 34);
            GeoSansLight73 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 73).deriveFont(attributes);
            GeoSansLight65 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 65).deriveFont(attributes);
            GeoSansLight96 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 96).deriveFont(attributes);
            GeoSansLight40 = loadFont(Arcaea.instance.getResource("fonts/GeosansLight.ttf"), 40).deriveFont(attributes);
            SourceHanCJK46 = loadFont(Arcaea.instance.getResource("fonts/SourceHanSansSC-Light-2.otf"), 46).deriveFont(attributes);
            SourceHanCJK96 = loadFont(Arcaea.instance.getResource("fonts/SourceHanSansSC-Light-2.otf"), 96);
            SourceHanCJK48 = loadFont(Arcaea.instance.getResource("fonts/SourceHanSansSC-Light-2.otf"), 48);
            SourceHanCJK36 = loadFont(Arcaea.instance.getResource("fonts/SourceHanSansSC-Light-2.otf"), 36);
            SourceHanCJK64 = loadFont(Arcaea.instance.getResource("fonts/SourceHanSansSC-Light-2.otf"), 64).deriveFont(attributes);
            getRecentTemplates();
        } catch (Exception e) {
            Arcaea.instance.logger.error("Could not read resource", e);
        }
    }

    private static final GaussianFilter gaussianBlur = new GaussianFilter(20);

    private static void getRecentTemplates() throws IOException {
        for (int i = 0; i < 23; i++) {
            File f = new File(ArcaeaHelper.mkdir("resources", "recentTemplates"), File.separator + i + ".png");
            if (!f.exists()) {
                Arcaea.instance.logger.warn("Recent template #{} was not found, generating", i);
                moe.hepta.graphics.Panel panel = new Panel(2560, 1440);
                BufferedImage src = ImageIO.read(Arcaea.instance.getResource("recent_bg/" + (i + 1) + ".jpg"));
                panel.draw(new ImageModel(gaussianBlur.filter(src, null), 0, 0, 2560, 1440));
                panel.draw(new ImageModel(Resources.recentTemplate, 0, 0, 2560, 1440));
                panel.output(0, f);
            }
            recentTemplates[i] = ImageIO.read(f);
        }
    }

    private static Font loadFont(InputStream f, float fontSize) throws IOException, FontFormatException {
        return Font.createFont(Font.TRUETYPE_FONT, f).deriveFont(fontSize);
    }
}
