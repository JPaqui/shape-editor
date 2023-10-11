package com.tolacombe.shapeeditor.shaperenderer;

import java.awt.*;
import java.io.Serializable;

public interface NativeShapeRenderer extends Serializable {
    void setBorderColor(Color color);
    void setFillColor(Color color);
    void setRotation(double rotationRadian);
    void addRectangle(double offsetX, double offsetY, double width, double height);
    void addEllipse(double offsetX, double offsetY, double radiusX, double radiusY);
    void addText(double offsetX, double offsetY, String text);
    void addCenteredText(double offsetX, double offsetY, String text);
    void startPolygon();
    void addVertexToPolygon(Point point);
    void endPolygon();
}
