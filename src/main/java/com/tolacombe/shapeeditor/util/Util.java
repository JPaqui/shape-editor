package com.tolacombe.shapeeditor.util;

import java.nio.file.Path;

public class Util {
    public static double ensureBoundsRotation(double rotation) {
        rotation %= Math.PI*2;
        if(rotation < 0) {
            rotation = Math.PI*2 - rotation;
        }
        return rotation;
    }

    public static Path getUserHome() {
        return Path.of(System.getProperty("user.home"));
    }
}
