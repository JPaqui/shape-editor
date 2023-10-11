package com.tolacombe.shapeeditor.action;

import com.tolacombe.shapeeditor.controller.Controller;
import com.tolacombe.shapeeditor.shape.EditorShape;
import com.tolacombe.shapeeditor.toolbar.Toolbar;
import com.tolacombe.shapeeditor.util.Area;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleAction implements Action {
    private final List<EditorShape> shapes;
    private final Map<EditorShape, Double> toolbarMults;
    private final Area selection;

    public SimpleAction(List<EditorShape> shapes, Map<EditorShape, Double> toolbarMults, Area selection) {
        this.selection = selection;
        this.shapes = new ArrayList<>();
        this.toolbarMults = new IdentityHashMap<>();
        for(EditorShape original : shapes) {
            EditorShape clone = original.clone();
            this.shapes.add(clone);
            if(toolbarMults.containsKey(original)) {
                this.toolbarMults.put(clone, toolbarMults.get(original));
            }
        }
    }

    @Override
    public void patch(State state) {
        Controller controller = state.getController();
        Toolbar toolbar = state.getToolbar();
        controller.clearCanvas();
        SimpleAction copy = new SimpleAction(shapes, toolbarMults, selection);
        for (EditorShape editorShape : copy.shapes){
            controller.addShape(editorShape);
        }
        toolbar.patch(copy.toolbarMults);
        state.getSelectionController().setSelection(controller, selection);
        controller.redraw();
    }

    @Override
    public String toString() {
        return shapes.stream().map(s -> s.getClass().getSimpleName() + "[x=" + s.getX() + ",y=" + s.getY()  + ",toolbar=" + toolbarMults.containsKey(s) + "]")
                .collect(Collectors.joining(", "))
                + ", Selection=" + selection;
    }
}
