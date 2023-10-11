package com.tolacombe.shapeeditor.action;

import com.tolacombe.shapeeditor.controller.Controller;
import com.tolacombe.shapeeditor.selection.SelectionController;
import com.tolacombe.shapeeditor.toolbar.Toolbar;

public class State {
    private final Controller controller;
    private final Toolbar toolbar;
    private final SelectionController selectionController;

    public State(Controller controller, Toolbar toolbar, SelectionController selectionController) {
        this.controller = controller;
        this.toolbar = toolbar;
        this.selectionController = selectionController;
    }

    public Controller getController() {
        return controller;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SelectionController getSelectionController() {
        return selectionController;
    }
}
