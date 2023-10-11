package com.tolacombe.shapeeditor.menubuilder;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;


public class MenuBuilderAWT implements MenuBuilder {
    private final JFrame jFrame;
    private final JPanel jPanel;
    private final Runnable redrawAction;
    private Runnable saveAction;

    public MenuBuilderAWT(Runnable redrawAction) {
        this.redrawAction = redrawAction;
        this.jFrame = new JFrame();
        this.jFrame.setSize(1000, 1000);
        this.jPanel = new JPanel();
        this.jFrame.add(jPanel);
    }

    @Override
    public void setTitle(String title) {
        this.jFrame.setTitle(title);
    }

    @Override
    public void addLabel(int x, int y, String label) {
        JLabel newLabel = new JLabel(label);
        newLabel.setLocation(x, y);
        this.jPanel.add(newLabel);
    }

    @Override
    public void addButtonWithText(int x, int y, String label, Runnable callback) {
        JButton btnNew = new JButton(label);
        btnNew.setLocation(x, y);
        btnNew.addActionListener(e -> {
            callback.run();
            MenuBuilderAWT.this.redrawAction.run();
            MenuBuilderAWT.this.saveAction.run();
        });
        this.jPanel.add(btnNew);
    }

    @Override
    public void addCheckbox(int x, int y, boolean defaultValue, Consumer<Boolean> callback) {
        JCheckBox jCheckBox = new JCheckBox();
        jCheckBox.setLocation(x, y);
        jCheckBox.setSelected(defaultValue);
        jCheckBox.addActionListener(e -> {
            callback.accept(jCheckBox.isSelected());
            MenuBuilderAWT.this.redrawAction.run();
            MenuBuilderAWT.this.saveAction.run();
        });
        this.jPanel.add(jCheckBox);
    }

    @Override
    public void addSlider(int x, int y, int width, double defaultValue, double min, double max, int majorTick, int minorTicksBetweenMajor, Consumer<Double> callback) {
        JSlider jSlider = new JSlider((int) min, (int) max);
        jSlider.setMajorTickSpacing(majorTick);
        jSlider.setMinorTickSpacing(majorTick / minorTicksBetweenMajor);
        jSlider.setSize(width, 10);
        jSlider.setLocation(x, y);
        jSlider.setValue((int) defaultValue);
        jSlider.addChangeListener(e -> {
            callback.accept((double) jSlider.getValue());
            MenuBuilderAWT.this.redrawAction.run();
        });
        jSlider.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {
                MenuBuilderAWT.this.saveAction.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}

        });
        this.jPanel.add(jSlider);
    }


    @Override
    public void addButtonWithIcon(int x, int y, String iconPath, Runnable callback) {
        JButton btnNew = new JButton(new ImageIcon(iconPath));
        btnNew.setLocation(x, y);
        btnNew.addActionListener(e -> {
            callback.run();
            MenuBuilderAWT.this.redrawAction.run();
            MenuBuilderAWT.this.saveAction.run();
        });
        this.jPanel.add(btnNew);
    }

    @Override
    public void addColorChooser(int x, int y, Color defaultColor, Consumer<Color> callback) {
        var iconColor = new Object() {
            Color color = defaultColor;
        };
        JButton button = new JButton(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(iconColor.color);
                g.fillRect(x, y, getIconWidth(), getIconHeight());
            }

            @Override
            public int getIconWidth() {
                return 50;
            }

            @Override
            public int getIconHeight() {
                return 10;
            }
        });
        button.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(jFrame, "Choisissez une couleur", defaultColor);
            if(newColor != null) {
                callback.accept(newColor);
                iconColor.color = newColor;
                button.repaint();
                MenuBuilderAWT.this.redrawAction.run();
                MenuBuilderAWT.this.saveAction.run();
            }
        });
        this.jPanel.add(button);
    }

    @Override
    public void display(Runnable saveAction) {
        this.saveAction = saveAction;
        this.jFrame.setVisible(true);
        int width = 0;
        int height = 0;
        for(Component c : this.jPanel.getComponents()) {
            width += c.getWidth();
            height += c.getHeight();
        }
        this.jFrame.setSize(width, height);
    }
}
