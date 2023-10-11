package com.tolacombe.shapeeditor.shape;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;

import java.awt.*;

public class EditorCircle extends AbstractSizedShape {
    public EditorCircle(double x, double y, Color color, double radius) {
        super(x, y, radius * 2, radius * 2, 0, color);
    }

    @Override
    public void createNativeShape(NativeShapeRenderer builder) {
        builder.setRotation(getRotation());
        builder.setFillColor(getColor());
        builder.addEllipse(getX(), getY(), getRadius(), getRadius());
    }

    public double getRadius() {
        return getWidth() / 2;
    }

    public void setRadius(double radius) {
        super.setWidth(radius*2);
        super.setHeight(radius*2);
    }

    @Override
    public void setWidth(double width) {
        setRadius(width/2);
    }

    @Override
    public void setHeight(double height) {
        setRadius(height/2);
    }

    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        menuBuilder.setTitle("Éditer le cercle");
        menuBuilder.addLabel(10, 20, "Couleur");
        menuBuilder.addColorChooser(100, 20, this.getColor(), this::setColor);
        menuBuilder.addLabel(10, 80, "Diamètre");
        menuBuilder.addSlider(100, 80, 100, getRadius() * 2, 10, maxY, maxY/5, 5, newDiameter -> setRadius(newDiameter/2));
    }
}
