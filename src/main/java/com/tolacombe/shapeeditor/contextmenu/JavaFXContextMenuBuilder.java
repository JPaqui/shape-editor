package com.tolacombe.shapeeditor.contextmenu;

import com.tolacombe.shapeeditor.controller.JavaFXAppInfo;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

public class JavaFXContextMenuBuilder implements ContextMenuBuilder {
    private final JavaFXAppInfo info;
    private final int canvasStartY;
    private final List<MenuItem> items = new ArrayList<>();

    public JavaFXContextMenuBuilder(JavaFXAppInfo info, int canvasStartY) {
        this.info = info;
        this.canvasStartY = canvasStartY;
    }

    @Override
    public void addButton(String text, Runnable callback) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(e -> callback.run());
        items.add(item);
    }

    @Override
    public void show(double x, double y) {
        ContextMenu contextMenu = new ContextMenu(items.toArray(MenuItem[]::new));
        Window window = info.getScene().getWindow();
        contextMenu.show(window, window.getX() + info.getLeftInset() + x, window.getY() + info.getTopInset() + canvasStartY + y);
    }
}
