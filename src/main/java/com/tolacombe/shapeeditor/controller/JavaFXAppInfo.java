package com.tolacombe.shapeeditor.controller;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXAppInfo {
    private final int leftInset;
    private final int rightInset;
    private final int topInset;
    private final int bottomInset;
    private final Stage stage;
    private final Scene scene;

    public JavaFXAppInfo(int leftInset, int rightInset, int topInset, int bottomInset, Stage stage, Scene scene) {
        this.leftInset = leftInset;
        this.rightInset = rightInset;
        this.topInset = topInset;
        this.bottomInset = bottomInset;
        this.stage = stage;
        this.scene = scene;
    }

    public int getLeftInset() {
        return leftInset;
    }

    public int getRightInset() {
        return rightInset;
    }

    public int getTopInset() {
        return topInset;
    }

    public int getBottomInset() {
        return bottomInset;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }
}
