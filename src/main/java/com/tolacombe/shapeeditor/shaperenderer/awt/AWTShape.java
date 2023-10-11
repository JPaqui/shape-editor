package com.tolacombe.shapeeditor.shaperenderer.awt;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AWTShape {
    private final List<AWTShapePart> parts = new ArrayList<>();

    public void add(Color fillColor, Color borderColor, double rotation, Shape shape) {
        this.parts.add(new AWTShapePart(fillColor, borderColor, rotation, shape));
    }

    public void draw(Graphics2D graphics) {
        this.parts.forEach(p -> p.draw(graphics));
    }

    public boolean contains(double x, double y) {
        return this.parts.stream().anyMatch(s -> s.contains(x, y));
    }
}
