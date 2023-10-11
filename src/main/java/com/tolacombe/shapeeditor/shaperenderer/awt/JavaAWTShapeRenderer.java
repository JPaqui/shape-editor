package com.tolacombe.shapeeditor.shaperenderer.awt;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;


public class JavaAWTShapeRenderer implements NativeShapeRenderer {
    private Color currentBorderColor = Color.BLACK;
    private Color currentFillColor = Color.WHITE;
    private AWTShape shapes = new AWTShape();
    private Polygon currentPolygon;
    private Color currentPolygonBorderColor;
    private Color currentPolygonFillColor;
    private double rotation;

    @Override
    public void setBorderColor(Color color) {
        this.currentBorderColor = color;
    }

    @Override
    public void setFillColor(Color color) {
        this.currentFillColor = color;
    }

    @Override
    public void setRotation(double rotationRadian) {
        this.rotation = rotationRadian;
    }

    @Override
    public void addRectangle(double offsetX, double offsetY, double width, double height) {
        this.shapes.add(this.currentFillColor, this.currentBorderColor, this.rotation, new Rectangle2D.Double(offsetX, offsetY, width, height));
    }

    @Override
    public void addEllipse(double offsetX, double offsetY, double radiusX, double radiusY) {
        this.shapes.add(this.currentFillColor, this.currentBorderColor, this.rotation, new Ellipse2D.Double(offsetX, offsetY, radiusX*2, radiusY*2));
    }

    @Override
    public void addText(double offsetX, double offsetY, String text) {
        //TODO
    }

    @Override
    public void addCenteredText(double offsetX, double offsetY, String text) {
        //TODO
    }

    @Override
    public void startPolygon() {
        this.currentPolygon = new Polygon();
        this.currentPolygonFillColor = this.currentFillColor;
        this.currentPolygonBorderColor = this.currentBorderColor;
    }

    @Override
    public void addVertexToPolygon(Point point) {
        this.currentPolygon.addPoint((int) point.getX(), (int) point.getY());
    }

    @Override
    public void endPolygon() {
        if(this.currentPolygon == null) {
            throw new IllegalStateException();
        }
        this.shapes.add(this.currentPolygonFillColor, this.currentPolygonBorderColor, this.rotation, this.currentPolygon);
        this.currentPolygon = null;
    }

    public AWTShape getShapes() {
        return this.shapes;
    }
}
