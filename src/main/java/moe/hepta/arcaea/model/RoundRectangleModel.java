package moe.hepta.arcaea.model;

import moe.hepta.graphics.IGraphicsModel;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundRectangleModel implements IGraphicsModel {
    private final Color color;
    private final int posX, posY, width, height, radius;

    public RoundRectangleModel(Color color, int posX, int posY, int width, int height, int radius) {
        this.color = color;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.radius = radius;
    }

    @Override
    public void draw(Graphics2D graphics2D) {
        graphics2D.setColor(this.color);
        graphics2D.fillRoundRect(posX, posY, width, height, radius, radius);
    }
}
