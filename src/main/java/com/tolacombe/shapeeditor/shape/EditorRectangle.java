package com.tolacombe.shapeeditor.shape;

import java.awt.*;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;

public class EditorRectangle extends AbstractSizedShape {
    public EditorRectangle(double x, double y, double width, double height, double rotation, Color color) {
        super(x, y, width, height, rotation, color);
    }

    @Override
    public void createNativeShape(NativeShapeRenderer builder) {
        builder.setRotation(getRotation());
        builder.setFillColor(getColor());
        builder.addRectangle(getX(), getY(), getWidth(), getHeight());
    }
    
    @Override
    public EditorRectangle clone() {
        return (EditorRectangle) super.clone();
    }

    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        super.populateMenu(menuBuilder, maxX, maxY);
        menuBuilder.setTitle("Éditer le rectangle");
    }
}
