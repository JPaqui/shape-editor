package com.tolacombe.shapeeditor.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class App extends Application {
    static JavaFXController controller;
    static String windowName;
    static int windowWidth;
    static int windowHeight;
    static Consumer<JavaFXAppInfo> init;

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(controller.getMainGroup());
        stage.setScene(scene);
        stage.setTitle(windowName);
        stage.show();
        double left = scene.getX();
        double right = scene.getWindow().getWidth()-scene.getWidth()-scene.getX();
        double top = scene.getY();
        double bottom = scene.getWindow().getHeight()-scene.getHeight()-scene.getY();
        stage.setWidth(windowWidth + left + right);
        stage.setHeight(windowHeight + + top + bottom);
        init.accept(new JavaFXAppInfo((int) left, (int) right, (int) top, (int) bottom, stage, scene));
    }
}
