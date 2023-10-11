package com.tolacombe.shapeeditor.menubuilder;

import java.awt.*;
import java.util.function.Consumer;

public interface MenuBuilder {
    void setTitle(String title);
    void addLabel(int x, int y, String label);
    void addButtonWithText(int x, int y, String label, Runnable callback);
    void addButtonWithIcon(int x, int y, String iconPath, Runnable callback);
    void addCheckbox(int x, int y, boolean defaultValue, Consumer<Boolean> callback);
    void addSlider(int x, int y, int width, double defaultValue, double min, double max, int majorTick, int minorTicksBetweenMajor, Consumer<Double> callback);
    void addColorChooser(int x, int y, Color defaultColor, Consumer<Color> callback);
    void display(Runnable saveAction);
}
