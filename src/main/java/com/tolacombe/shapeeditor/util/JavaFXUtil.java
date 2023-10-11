package com.tolacombe.shapeeditor.util;

import javafx.scene.paint.Color;

public class JavaFXUtil {
    public static Color convertAWTToJavaFXColor(java.awt.Color color) {
        return Color.color(
                color.getRed() / 255.0,
                color.getGreen() / 255.0,
                color.getBlue() / 255.0,
                color.getAlpha() / 255.0
        );
    }
    public static java.awt.Color convertJavaFXToAWTColor(Color color) {
        return new java.awt.Color(
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255),
                (int)(color.getOpacity() * 255)
        );
    }
}
