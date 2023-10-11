package com.tolacombe.shapeeditor.contextmenu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AWTContextMenuBuilder implements ContextMenuBuilder {
    private final Panel panel;
    private final List<JMenuItem> items = new ArrayList<>();

    public AWTContextMenuBuilder(Panel panel) {
        this.panel = panel;
    }

    @Override
    public void addButton(String text, Runnable callback) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(e -> callback.run());
        this.items.add(item);
    }

    @Override
    public void show(double x, double y) {
        JPopupMenu contextMenu = new JPopupMenu();
        this.items.forEach(contextMenu::add);
        contextMenu.show(panel, (int) x, (int) y);
    }
}
