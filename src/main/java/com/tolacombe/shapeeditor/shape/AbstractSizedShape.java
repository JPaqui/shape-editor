package com.tolacombe.shapeeditor.shape;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;

import java.awt.*;

abstract class AbstractSizedShape extends AbstractShape {

    private double width;
    private double height;

    protected AbstractSizedShape(double x, double y, double width, double height, double rotation, Color color) {
        super(x, y, rotation, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public double getCenterX() {
        return getX() + getWidth() / 2;
    }

    @Override
    public void setCenterX(double x) {
        setX(x - getWidth() / 2);
    }

    @Override
    public double getCenterY() {
        return getY() + getHeight() / 2;
    }

    @Override
    public void setCenterY(double y) {
        setY(y - getHeight() / 2);
    }

    @Override
    public void createNativeShape(NativeShapeRenderer builder) {

    }

    @Override
    public AbstractSizedShape clone() {
        return (AbstractSizedShape) super.clone();
    }

    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        super.populateMenu(menuBuilder, maxX, maxY);
        menuBuilder.addLabel(10, 80, "Largeur");
        menuBuilder.addSlider(100, 80, 100, this.width, 10, maxX, maxX/5, 5, this::setWidth);
        menuBuilder.addLabel(10, 100, "Hauteur");
        menuBuilder.addSlider(100, 100, 100, this.height, 10, maxY, maxY/5, 5, this::setHeight);
    }
}
