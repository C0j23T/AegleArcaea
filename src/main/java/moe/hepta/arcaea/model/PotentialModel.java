package moe.hepta.arcaea.model;

import moe.hepta.arcaea.Resources;
import moe.hepta.arcaea.utils.ArcaeaHelper;
import moe.hepta.graphics.IGraphicsModel;
import moe.hepta.graphics.Panel;
import moe.hepta.graphics.model.ImageModel;
import moe.hepta.graphics.model.TextWithStrokeModel;

import java.awt.*;

public class PotentialModel implements IGraphicsModel {

    private final int posX, posY, size;
    private final short potential;

    public PotentialModel(short potential, int posX, int posY, int size) {
        this.potential = potential;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
    }

    @Override
    public void draw(Graphics2D g) {
        Panel temp = new Panel(200, 200);
        temp.draw(new IGraphicsModel[]{new ImageModel(ArcaeaHelper.ratingImage(potential), 21, 21, 158)});
        if (potential < 0)
            temp.draw(new IGraphicsModel[]{new TextWithStrokeModel("--", Resources.Exo54, Color.WHITE, 76, 116, new Color(31, 30, 51), 8)});
        else {
            char[] ptt = String.valueOf(potential + 10000).toCharArray();
            String l = (ptt[1] == '0' ? String.valueOf(ptt[2]) : String.valueOf(ptt[1]) + ptt[2]), r = "." + ptt[3] + ptt[4];
            FontMetrics fontMetrics = g.getFontMetrics(Resources.Exo54);
            int lx = fontMetrics.stringWidth(l);
            fontMetrics = g.getFontMetrics(Resources.Exo38);
            int rx = fontMetrics.stringWidth(r);
            int posX = 95 - (lx + rx) / 2;
            Color stroke = potential < 1300 ? new Color(31, 30, 51) : new Color(104, 9, 52);
            temp.draw(new IGraphicsModel[]{new TextWithStrokeModel(l, Resources.Exo54, Color.WHITE, posX, 116, stroke, 8),
                    new TextWithStrokeModel(r, Resources.Exo38, Color.WHITE, 94, 116, stroke, 8)});
        }
        g.drawImage(temp.getBufferedImage(), posX, posY, size, size, null);
    }
}