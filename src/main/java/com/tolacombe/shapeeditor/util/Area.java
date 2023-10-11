package com.tolacombe.shapeeditor.util;

import com.tolacombe.shapeeditor.shape.EditorShape;

import java.awt.*;

public class Area {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    public Area(Rectangle rectangle) {
        this(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }

    public Area(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public Area withY1(int y1) {
        return new Area(x1, y1, x2, y2);
    }

    public int getX2() {
        return x2;
    }

    public Area withX2(int x2) {
        return new Area(x1, y1, x2, y2);
    }

    public int getY2() {
        return y2;
    }

    public Area withY2(int y2) {
        return new Area(x1, y1, x2, y2);
    }
    public boolean contains(EditorShape shape) {
        Rectangle rectangle = new Rectangle((int) shape.getX(), (int) shape.getY(), (int) shape.getWidth(), (int) shape.getHeight());
        return toRectangle().contains(rectangle);
    }
    public boolean intersects(EditorShape shape) {
        Rectangle rectangle = new Rectangle((int) shape.getX(), (int) shape.getY(), (int) shape.getWidth(), (int) shape.getHeight());
        return toRectangle().intersects(rectangle);
    }
    public Rectangle toRectangle() {
        int upLeftX = Math.min(x1, x2);
        int upLeftY = Math.min(y1, y2);
        int downRightX = Math.max(x1, x2);
        int downRightY = Math.max(y1, y2);
        return new Rectangle(upLeftX, upLeftY, downRightX - upLeftX, downRightY - upLeftY);
    }

    @Override
    public String toString() {
        return "Area[x1=" + getX1() + ",y1=" + getY1() + ",x2=" + getX2() + ",y2=" + getY2() + "]";
    }
}
