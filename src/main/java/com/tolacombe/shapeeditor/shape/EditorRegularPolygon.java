package com.tolacombe.shapeeditor.shape;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;

import java.awt.*;


public class EditorRegularPolygon extends AbstractShape {
    private int sideLength;
    private int sideCount;
    public EditorRegularPolygon(double x, double y, int sideLength, int sideCount, double rotation, Color color) {
        super(x, y, rotation, color);
        this.sideLength = sideLength;
        this.sideCount = sideCount;
    }
    public double getSegmentAngle() {
        return Math.PI * 2 / sideCount;
    }
    public double getRadius() {
        double segmentAngle = getSegmentAngle();
        return sideLength * Math.sin((Math.PI - segmentAngle)/2) / Math.sin(segmentAngle);
    }
    public void setRadius(double radius) {
        sideLength = (int)Math.round(Math.sqrt(Math.pow(radius, 2) + Math.pow(radius, 2) - 2 * radius * radius *Math.cos(getSegmentAngle())));
    }

    public int getSideLength() {
        return sideLength;
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    @Override
    public double getWidth() {
        return getRadius()*2;
    }

    @Override
    public void setWidth(double width) {
        setRadius(width/2);
    }

    @Override
    public double getHeight() {
        return getRadius()*2;
    }

    @Override
    public void setHeight(double height) {
        setRadius(height/2);
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
        double segmentAngle = getSegmentAngle();
        double radius = getRadius();
        double centerX = getX() + radius;
        double centerY = getY() + radius;
        builder.setRotation(getRotation());
        builder.setFillColor(getColor());
        builder.startPolygon();
        for(int i = 0; i < sideCount; ++i) {
            double angle = segmentAngle * i;
            builder.addVertexToPolygon(new Point((int)(centerX + Math.cos(angle) * radius), (int)(centerY + Math.sin(angle) * radius)));
        }
        builder.endPolygon();
    }
    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        super.populateMenu(menuBuilder, maxX, maxY);
        menuBuilder.setTitle("Éditer le polygone régulier");
        menuBuilder.addLabel(10, 80, "Longueur coté");
        menuBuilder.addSlider(100, 80, 100, this.sideLength, 10, maxX, maxX/5, 5, newSideLength -> setSideLength((int)(double)newSideLength));
        menuBuilder.addLabel(10, 100, "Nombre cotés");
        menuBuilder.addSlider(100, 100, 100, this.sideCount, 3, 24, 8, 2, newSideCount -> setSideCount((int)(double)newSideCount));
        menuBuilder.addLabel(10, 120, "Rotation");
        menuBuilder.addSlider(100, 120, 100, this.sideCount, 3, 24, 8, 2, rotation -> setRotation(rotation));
    }

}
