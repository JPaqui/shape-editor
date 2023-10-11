package com.tolacombe.shapeeditor;

import com.tolacombe.shapeeditor.controller.ControllerFactory;

public class Main {
    public static void main(String[] args) {
        ControllerFactory factory = new ControllerFactory();
        new XShape().launch(factory.createAWTController(), "XShape Version AWT", 500, 500, 50);
        new XShape().launch(factory.createJavaFXController(), "XShape Version JavaFX", 500, 500, 50);
    }
}
