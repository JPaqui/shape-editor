package com.tolacombe.shapeeditor.shaperenderer.awt;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class AWTShapePart {
    private final Color fillColor;
    private final Color borderColor;
    private final double rotation;
    private final Shape shape;

    public AWTShapePart(Color fillColor, Color borderColor, double rotation, Shape shape) {
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        this.rotation = rotation;
        this.shape = shape;
    }

    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(1));
        graphics.setTransform(new AffineTransform());

        // Rectangle
        if(this.shape instanceof Rectangle2D.Double) {
            Rectangle2D.Double rect = (Rectangle2D.Double) this.shape;
            graphics.rotate(this.rotation, rect.getCenterX(), rect.getCenterY());
            graphics.setColor(fillColor);
            graphics.fillRect((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
            graphics.setColor(borderColor);
            graphics.drawRect((int) rect.x, (int) rect.y, (int) rect.width, (int) rect.height);
        }

        // Circle
        else if (this.shape instanceof Ellipse2D.Double){
            Ellipse2D.Double circle = (Ellipse2D.Double) this.shape;
            graphics.rotate(this.rotation, circle.getCenterX(), circle.getCenterY());
            graphics.setColor(fillColor);
            graphics.fillOval((int) circle.x, (int) circle.y, (int) circle.width, (int) circle.height);
            graphics.setColor(borderColor);
            graphics.drawOval((int) circle.x, (int) circle.y, (int) circle.width, (int) circle.height);
        }

        // Polygon
        else if (this.shape instanceof Polygon){
            Polygon polygon = (Polygon) this.shape;
            graphics.rotate(this.rotation, polygon.getBounds().getCenterX(), polygon.getBounds().getCenterY());
            graphics.setColor(fillColor);
            graphics.fillPolygon(polygon);
            graphics.setColor(borderColor);
            graphics.drawPolygon(polygon);
        }


    }

    public boolean contains(double x, double y) {
        return this.shape.contains(x, y);
    }
}
