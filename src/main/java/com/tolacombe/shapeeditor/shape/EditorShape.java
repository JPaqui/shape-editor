package com.tolacombe.shapeeditor.shape;

import java.awt.*;
import java.io.Serializable;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;

public interface EditorShape extends Cloneable, Serializable {
    double getX();
    void setX(double x);
    double getY();
    void setY(double y);
    double getWidth();
    void setWidth(double width);
    double getHeight();
    void setHeight(double height);
    double getCenterX();
    void setCenterX(double x);
    double getCenterY();
    void setCenterY(double y);
    Color getColor();
    void setColor(Color color);
    EditorShape clone();
    void createNativeShape(NativeShapeRenderer builder);
    void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY);
    double getRotation();
    void setRotation(double rotate);
}
