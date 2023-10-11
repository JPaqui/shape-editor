package com.tolacombe.shapeeditor.toolbar;

import com.tolacombe.shapeeditor.shape.EditorShape;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Toolbar {
    private final int width;
    private final int shapeSize;
    private final int shapesByRow;
    private final int horizontalGapCount;
    private final int gapSize;
    private final List<ToolbarShape> shapes;

    public Toolbar(int width, int shapeSize, int shapesByRow) {
        if(shapesByRow * shapeSize > width) throw new IllegalArgumentException();
        this.width = width;
        this.shapeSize = shapeSize;
        this.horizontalGapCount = 2 + shapesByRow; // one left + one right + one per shape
        this.gapSize = (width - shapeSize * shapesByRow) / this.horizontalGapCount;
        this.shapesByRow = shapesByRow;
        this.shapes = new ArrayList<>();
    }
    public int getShapeSize() {
        return this.shapeSize;
    }
    public Stream<EditorShape> allShapes() {
        return this.shapes.stream().map(ToolbarShape::getShape);
    }
    public boolean isInToolbarRange(EditorShape shape) {
        return shape.getX() <= width;
    }
    public boolean isNotInToolbarRange(EditorShape shape) {
        return shape.getX() > width;
    }
    public ToolbarPutResult tryPutInToolbar(EditorShape shape, double mouseX, double mouseY) {
        if(isNotInToolbarRange(shape)) {
            return new ToolbarPutResult(false, null);
        }
        double max = Math.max(shape.getWidth(), shape.getHeight());
        double mult = max / shapeSize;
        shape.setWidth(shapeSize);
        shape.setHeight(shapeSize);
        int idx = shapes.size();

        mouseX = Math.max(0, mouseX);
        mouseY = Math.max(0, mouseY);
        mouseX = Math.min(width, mouseX);
        int x = (int) (mouseX / (this.gapSize + this.shapeSize));
        int y = (int) (mouseY / (this.gapSize + this.shapeSize));
        ToolbarPutResult result;
        if(y * this.shapesByRow + x > idx - 1) {
            x = getX(idx);
            y = getY(idx);
            result = new ToolbarPutResult(true, null);
            shapes.add(new ToolbarShape(shape, mult));
        } else {
            ToolbarShape previous = this.shapes.set(y * this.shapesByRow + x, new ToolbarShape(shape, mult));
            result = new ToolbarPutResult(true, previous.getShape());
        }
        shape.setX(this.gapSize + x * (this.shapeSize + this.gapSize));
        shape.setY(this.gapSize + y * (this.shapeSize + this.gapSize));
        return result;
    }
    public EditorShape tryGetFromToolbar(EditorShape shape) {
        if(isNotInToolbarRange(shape)) {
            return null;
        }
        ToolbarShape toolbarShape = shapes.stream().filter(s -> s.getShape() == shape).findFirst().orElseThrow();
        EditorShape newShape = shape.clone();
        newShape.setWidth(shapeSize * toolbarShape.getSizeMult());
        newShape.setHeight(shapeSize * toolbarShape.getSizeMult());
        return newShape;
    }
    public Map<EditorShape, Double> getShapesMults() {
        Map<EditorShape, Double> map = new IdentityHashMap<>();
        this.shapes.forEach(s -> map.put(s.getShape(), s.getSizeMult()));
        return map;
    }
    public void patch(Map<EditorShape, Double> shapesMults) {
        this.shapes.clear();
        shapesMults.forEach((shape, shapesMult) -> this.shapes.add(new ToolbarShape(shape, shapesMult)));
    }

    private int getX(int idx) {
        return idx % shapesByRow;
    }
    private int getY(int idx) {
        return idx / shapesByRow;
    }
}
