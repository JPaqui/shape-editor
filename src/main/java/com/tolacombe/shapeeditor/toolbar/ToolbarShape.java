package com.tolacombe.shapeeditor.toolbar;

import com.tolacombe.shapeeditor.shape.EditorShape;

public class ToolbarShape {
    private final EditorShape shape;
    private final double sizeMult;

    public ToolbarShape(EditorShape shape, double sizeMult) {
        this.shape = shape;
        this.sizeMult = sizeMult;
    }

    public EditorShape getShape() {
        return shape;
    }

    public double getSizeMult() {
        return sizeMult;
    }
}
