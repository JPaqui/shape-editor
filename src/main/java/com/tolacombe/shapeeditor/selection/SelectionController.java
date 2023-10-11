package com.tolacombe.shapeeditor.selection;

import com.tolacombe.shapeeditor.controller.Controller;
import com.tolacombe.shapeeditor.util.Area;

public class SelectionController {
    private Area area;

    public Area getArea() {
        return area;
    }
    public void setSelection(Controller controller, Area area) {
        this.area = area;
        controller.setSelectBox(area);
    }
}
