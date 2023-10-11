package com.tolacombe.shapeeditor.shape;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;
import com.tolacombe.shapeeditor.util.Util;

public class EditorGroupShape implements EditorShape {
    private List<EditorShape> editorShapes;
    private double x;
    private double y;
    private double size;
    private double rotation;
    public EditorGroupShape(List<EditorShape> list){
        this.editorShapes = list.stream().map(EditorShape::clone).collect(Collectors.toList());
        this.x = Double.POSITIVE_INFINITY;
        this.y = Double.POSITIVE_INFINITY;
        for (EditorShape editorShape : editorShapes){
            this.x = Math.min(this.x, editorShape.getX());
            this.y = Math.min(this.y, editorShape.getY());
        }
        for (EditorShape editorShape : editorShapes){
            editorShape.setX(editorShape.getX() - this.x);
            editorShape.setY(editorShape.getY() - this.y);
        }
        for (EditorShape editorShape : editorShapes){
            this.size = Math.max(this.size, editorShape.getX() + editorShape.getWidth());
            this.size = Math.max(this.size, editorShape.getY() + editorShape.getHeight());
        }
    }
    public List<EditorShape> ungroup() {
        return this.editorShapes.stream()
                .map(s -> {
                    EditorShape clone = s.clone();
                    clone.setX(this.x + clone.getX());
                    clone.setY(this.y + clone.getY());
                    return clone;
                })
                .collect(Collectors.toList());
    }
    public void modifyAll(Consumer<EditorShape> consumer) {
        this.editorShapes.forEach(consumer);
    }
    @Override
    public double getX() {
        return this.x;
    }
    @Override
    public void setX(double x) {
        //double diff = this.x - x;
        this.x = x;
        //modifyAll(s -> s.setX(s.getX()+diff));
    }
    @Override
    public double getY() {
        return this.y;
    }
    @Override
    public void setY(double y) {
        //double diff = this.y - y;
        this.y = y;
        //modifyAll(s -> s.setY(s.getY()+diff));
    }
    public double getSize() {
        return this.size;
    }
    public void setSize(double size) {
        if(size <= 0) throw new IllegalArgumentException();
        double mult = size / this.size;
        this.size = size;
        modifyAll(s -> {
            // Needs to be done for both before because some shapes may only have one size field
            double width = s.getWidth();
            double height = s.getHeight();
            s.setWidth(width * mult);
            s.setX(s.getX() * mult);
            s.setHeight(height * mult);
            s.setY(s.getY() * mult);
        });
    }

    @Override
    public double getWidth() {
        return this.size;
    }

    @Override
    public void setWidth(double width) {
        setSize(width);
    }

    @Override
    public double getHeight() {
        return this.size;
    }

    @Override
    public void setHeight(double height) {
        setSize(height);
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
    public double getRotation() {
        return this.rotation;
    }
    @Override
    public void setRotation(double rotate) {
        this.rotation = Util.ensureBoundsRotation(rotate);
        modifyAll(s -> s.setRotation(rotation));
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("unsupported method 'getColor'");
    }
    @Override
    public void setColor(Color color) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("unsupported method 'setColor'");
    }
    @Override
    public void createNativeShape(NativeShapeRenderer builder) {
        double angle = getRotation();
        double centerX = getWidth()/2;
        double centerY = getHeight()/2;
        for (EditorShape editorShape : editorShapes) {
            double shapeX = editorShape.getX();
            double shapeY = editorShape.getY();
            double shapeAngle = Math.atan2(centerX - editorShape.getCenterX(), editorShape.getCenterY() - centerY) + Math.PI/2;
            double shapeDist = Math.hypot(centerX - editorShape.getCenterX(), centerY - editorShape.getCenterY());
            editorShape.setCenterX(getCenterX() + shapeDist * Math.cos(shapeAngle + angle));
            editorShape.setCenterY(getCenterY() + shapeDist * Math.sin(shapeAngle + angle));
            editorShape.createNativeShape(builder);
            editorShape.setX(shapeX);
            editorShape.setY(shapeY);
        }
    }
    @Override
    public EditorGroupShape clone() {
        try {
            EditorGroupShape clone = (EditorGroupShape) super.clone();
            clone.editorShapes = clone.editorShapes.stream()
                    .map(EditorShape::clone)
                    .collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void populateMenu(MenuBuilder menuBuilder, int maxX, int maxY) {
        menuBuilder.setTitle("Groupe de formes");
        menuBuilder.addLabel(10, 20, "Taille");
        menuBuilder.addSlider(100, 20, 100, this.size, 10, maxX, maxX/5, 5, this::setSize);
        menuBuilder.addLabel(10, 40, "Rotation");
        menuBuilder.addSlider(100, 40, 100, Math.toDegrees(this.rotation), 0, 360, 90, 4, newDegrees -> setRotation(Math.toRadians(newDegrees)));
    }
}
