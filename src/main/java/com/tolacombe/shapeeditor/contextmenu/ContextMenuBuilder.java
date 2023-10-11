package com.tolacombe.shapeeditor.contextmenu;

public interface ContextMenuBuilder {
    void addButton(String text, Runnable callback);
    void show(double x, double y);
}
