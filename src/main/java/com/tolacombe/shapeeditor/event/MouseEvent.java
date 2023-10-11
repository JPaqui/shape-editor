package com.tolacombe.shapeeditor.event;

public class MouseEvent {
    private final MouseButtonType buttonType;
    private final double x;
    private final double y;

    public MouseEvent(MouseButtonType buttonType, double x, double y) {
        this.buttonType = buttonType;
        this.x = x;
        this.y = y;
    }

    public MouseButtonType getButtonType() {
        return this.buttonType;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }
}
