package com.tolacombe.shapeeditor.controller;

import com.tolacombe.shapeeditor.contextmenu.ContextMenuBuilder;
import com.tolacombe.shapeeditor.event.MouseEvent;
import com.tolacombe.shapeeditor.filedialog.XShapeFileDialogFactory;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.shape.EditorShape;
import com.tolacombe.shapeeditor.util.Area;

import java.awt.*;
import java.io.InputStream;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.List;

public interface Controller extends Serializable {
    void launch(String windowName, int windowWidth, int windowHeight, Runnable init);
    void addButton(double x, double y, String text, Runnable callback);
    void addButtonWithIcon(double x, double y,double width, double height, InputStream inputStream, Runnable callback);
    void addPNGImage(double x, double y, double width, double height, InputStream inputStream);
    void createCanvas(double x, double y, double width, double height, Color backgroundColor);
    void addFixedRectangle(FixedRectangle fixedRectangle);
    void addShape(EditorShape shape);
    void update(EditorShape shape);
    void removeShape(EditorShape shape);
    void removeAllShapesExcept(List<EditorShape> shapes);
    void redraw();
    void clearCanvas();
    void setSelectBox(Area area);
    void setOnCanvasClick(Consumer<MouseEvent> callback);
    void setOnCanvasMousePressed(Consumer<MouseEvent> callback);
    void setOnCanvasMouseReleased(Consumer<MouseEvent> callback);
    void setOnShapeClick(BiConsumer<EditorShape, MouseEvent> callback);
    void setOnShapeMousePressed(BiConsumer<EditorShape, MouseEvent> callback);
    void setOnShapeMouseReleased(BiConsumer<EditorShape, MouseEvent> callback);
    void setOnDrag(Consumer<MouseEvent> callback);
    void setOnShutdown(Runnable callback);
    Stream<EditorShape> shapes();
    ContextMenuBuilder getContextMenuBuilder();
    XShapeFileDialogFactory getFileDialogFactory();
    MenuBuilder getMenuBuilder();
    void alert(String message);
}
