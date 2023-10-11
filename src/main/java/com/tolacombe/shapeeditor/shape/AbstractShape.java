package com.tolacombe.shapeeditor.shape;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.util.Util;

import java.awt.*;

abstract class AbstractShape implements EditorShape {

    private double x;
    private double y;
    private double rotation;
    private Color color;

    protected AbstractShape(double x, double y, double rotation, Color color) {
        this.x = x;
        this.y = y;
        this.rotation = Util.ensureBoundsRotation(rotation);
        this.color = color;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(double rotation) {
        this.rotation = Util.ensureBoundsRotation(rotation);
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public AbstractShape clone() {
        try {
            return (AbstractShape) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        // X and Y shouldn't to be modified here
        menuBuilder.addLabel(10, 20, "Rotation");
        menuBuilder.addSlider(100, 20, 100, Math.toDegrees(this.rotation), 0, 360, 90, 4, newDegrees -> setRotation(Math.toRadians(newDegrees)));
        menuBuilder.addLabel(10, 40, "Couleur");
        menuBuilder.addColorChooser(100, 40, this.color, this::setColor);
    }
}
