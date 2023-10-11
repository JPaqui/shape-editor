package com.tolacombe.shapeeditor.menubuilder;

import com.tolacombe.shapeeditor.util.JavaFXUtil;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.util.function.Consumer;

public class MenuBuilderFX implements MenuBuilder {
    private final Group pane;
    private final Stage stage;
    private final Runnable redrawAction;
    private Runnable saveAction;

    public MenuBuilderFX(Runnable redrawAction) {
        this.redrawAction = redrawAction;
        this.stage = new Stage();
        this.pane = new Group();
        Scene scene = new Scene(pane);
        this.stage.setScene(scene);
    }

    @Override
    public void setTitle(String title) {
        this.stage.setTitle(title);
    }

    @Override
    public void addLabel(int x, int y, String label) {
        Label newLabel = new Label(label);
        newLabel.setTranslateX(x);
        newLabel.setTranslateY(y);
        this.pane.getChildren().add(newLabel);
    }

    @Override
    public void addButtonWithText(int x, int y, String label, Runnable callback) {
        Button button = new Button(label);
        button.setTranslateX(y);
        button.setTranslateY(y);
        button.setOnAction(e -> {
            callback.run();
            MenuBuilderFX.this.redrawAction.run();
            MenuBuilderFX.this.saveAction.run();
        });
        this.pane.getChildren().add(button);
    }

    @Override
    public void addCheckbox(int x, int y, boolean defaultValue, Consumer<Boolean> callback) {
        CheckBox checkBox = new CheckBox();
        checkBox.setTranslateX(x);
        checkBox.setTranslateY(y);
        checkBox.setSelected(defaultValue);
        checkBox.selectedProperty().addListener(e -> {
            callback.accept(checkBox.isSelected());
            MenuBuilderFX.this.redrawAction.run();
            MenuBuilderFX.this.saveAction.run();
        });
        this.pane.getChildren().add(checkBox);
    }

    @Override
    public void addSlider(int x, int y, int width, double defaultValue, double min, double max, int majorTick, int minorTicksBetweenMajor, Consumer<Double> callback) {
        Slider slider = new Slider();
        slider.setMin(min);
        slider.setMax(max);
        slider.setTranslateX(x);
        slider.setTranslateY(y);
        slider.setMajorTickUnit(majorTick);
        slider.setMinorTickCount(minorTicksBetweenMajor);
        slider.setValue(defaultValue);
        slider.valueProperty().addListener(e -> {
            MenuBuilderFX.this.redrawAction.run();
            callback.accept(slider.getValue());
        });
        slider.setOnMouseReleased(e -> MenuBuilderFX.this.saveAction.run());

        this.pane.getChildren().add(slider);
    }

    @Override
    public void addButtonWithIcon(int x, int y, String iconPath, Runnable callback) {
        Button button = new Button("", new ImageView(iconPath));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setTranslateX(y);
        button.setTranslateY(y);
        button.setOnAction(e -> {
            callback.run();
            MenuBuilderFX.this.redrawAction.run();
            MenuBuilderFX.this.saveAction.run();
        });
        this.pane.getChildren().add(button);
    }

    @Override
    public void addColorChooser(int x, int y, Color defaultColor, Consumer<Color> callback) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setTranslateX(x);
        colorPicker.setTranslateY(y);
        colorPicker.setValue(JavaFXUtil.convertAWTToJavaFXColor(defaultColor));
        colorPicker.setOnAction(e -> {
            callback.accept(JavaFXUtil.convertJavaFXToAWTColor(colorPicker.getValue()));
            MenuBuilderFX.this.redrawAction.run();
            MenuBuilderFX.this.saveAction.run();
        });
        this.pane.getChildren().add(colorPicker);
    }

    @Override
    public void display(Runnable saveAction) {
        this.saveAction = saveAction;
        this.stage.show();
        this.stage.sizeToScene();
        this.stage.setWidth(this.stage.getWidth() + 10);
        this.stage.setHeight(this.stage.getHeight() + 20);
    }
}
