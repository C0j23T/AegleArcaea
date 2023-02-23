package moe.hepta.arcaea.model;

import moe.hepta.graphics.IGraphicsModel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class RoundImageModel implements IGraphicsModel {
    private final BufferedImage src;
    private final int posX, posY, width, height, radius;

    public RoundImageModel(BufferedImage src, int posX, int posY, int width, int height, int radius) {
        this.src = src;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.radius = radius;
    }
    @Override
    public void draw(Graphics2D graphics2D) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = output.createGraphics();
        g2d.setComposite(AlphaComposite.Src);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));
        g2d.setComposite(AlphaComposite.SrcAtop);
        g2d.drawImage(src, 0, 0, width, height, null);
        g2d.dispose();
        graphics2D.drawImage(output, posX, posY, width, height, null);
    }
}
