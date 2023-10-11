package com.tolacombe.shapeeditor.toolbar;

import com.tolacombe.shapeeditor.shape.EditorShape;

public class ToolbarPutResult {
    private final boolean shapePut;
    private final  EditorShape removed;

    public ToolbarPutResult(boolean shapePut, EditorShape removed) {
        this.shapePut = shapePut;
        this.removed = removed;
    }

    public boolean isShapePut() {
        return shapePut;
    }

    public EditorShape getRemoved() {
        return removed;
    }
}
