package com.tolacombe.shapeeditor.controller;

public class ControllerFactory {
    public Controller createJavaFXController() {
        return new JavaFXController();
    }
    public Controller createAWTController() {
        return new JavaAWTController();
    }
}
