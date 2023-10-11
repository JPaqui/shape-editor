package com.tolacombe.shapeeditor.action;

import com.tolacombe.shapeeditor.util.Area;

public class SelectionAction implements Action {
    private final Area area;

    public SelectionAction(Area area) {
        this.area = area;
    }

    @Override
    public void patch(State state) {
        state.getSelectionController().setSelection(state.getController(), area);
    }
}
