package com.tolacombe.shapeeditor.shaperenderer.javafx;

import com.tolacombe.shapeeditor.shaperenderer.NativeShapeRenderer;
import com.tolacombe.shapeeditor.util.JavaFXUtil;
import javafx.scene.Group;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.awt.*;


public class JavaFXShapeRenderer implements NativeShapeRenderer {
    private javafx.scene.paint.Color currentBorderColor = javafx.scene.paint.Color.BLACK;
    private javafx.scene.paint.Color currentFillColor = javafx.scene.paint.Color.WHITE;
    private double rotation;
    private final Group group = new Group();
    private Polygon currentPolygon;
    @Override
    public void setBorderColor(Color color) {
        this.currentBorderColor = JavaFXUtil.convertAWTToJavaFXColor(color);
    }

    @Override
    public void setFillColor(Color color) {
        this.currentFillColor = JavaFXUtil.convertAWTToJavaFXColor(color);
    }

    @Override
    public void setRotation(double rotationRadian) {
        this.rotation = Math.toDegrees(rotationRadian);
    }

    @Override
    public void addRectangle(double offsetX, double offsetY, double width, double height) {
        Rectangle rectangle = new Rectangle(offsetX, offsetY, width, height);
        rectangle.setRotate(this.rotation);
        rectangle.setFill(this.currentFillColor);
        rectangle.setStroke(this.currentBorderColor);
        this.group.getChildren().add(rectangle);
    }

    @Override
    public void addEllipse(double offsetX, double offsetY, double radiusX, double radiusY) {
        Ellipse ellipse = new Ellipse(offsetX + radiusX, offsetY + radiusX, radiusX, radiusY);
        ellipse.setRotate(this.rotation);
        ellipse.setFill(this.currentFillColor);
        ellipse.setStroke(this.currentBorderColor);
        this.group.getChildren().add(ellipse);
    }

    @Override
    public void addText(double offsetX, double offsetY, String text) {
        Text textNode = new Text(offsetX, offsetY, text);
        textNode.setFill(this.currentFillColor);
        textNode.setStroke(this.currentBorderColor);
        this.group.getChildren().add(textNode);
    }

    @Override
    public void addCenteredText(double offsetX, double offsetY, String text) {
        //TODO
    }

    @Override
    public void startPolygon() {
        this.currentPolygon = new Polygon();
        this.currentPolygon.setFill(this.currentFillColor);
        this.currentPolygon.setStroke(this.currentBorderColor);
        this.currentPolygon.setRotate(this.rotation);
    }

    @Override
    public void addVertexToPolygon(Point point) {
        this.currentPolygon.getPoints().add(point.getX());
        this.currentPolygon.getPoints().add(point.getY());
    }

    @Override
    public void endPolygon() {
        if(this.currentPolygon == null) {
            throw new IllegalStateException();
        }
        this.group.getChildren().add(this.currentPolygon);
        this.currentPolygon = null;
    }
    
    public Group getGroup() {
        return this.group;
    }
}
