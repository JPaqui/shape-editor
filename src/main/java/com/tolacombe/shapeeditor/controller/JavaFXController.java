package com.tolacombe.shapeeditor.controller;

import com.tolacombe.shapeeditor.contextmenu.ContextMenuBuilder;
import com.tolacombe.shapeeditor.contextmenu.JavaFXContextMenuBuilder;
import com.tolacombe.shapeeditor.event.MouseButtonType;
import com.tolacombe.shapeeditor.event.MouseEvent;
import com.tolacombe.shapeeditor.filedialog.XShapeFileDialogFactory;
import com.tolacombe.shapeeditor.filedialog.XShapeFileDialogFactoryFX;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilderFX;
import com.tolacombe.shapeeditor.shape.EditorShape;
import com.tolacombe.shapeeditor.shaperenderer.javafx.JavaFXShapeRenderer;
import com.tolacombe.shapeeditor.util.Area;
import com.tolacombe.shapeeditor.util.JavaFXUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineJoin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

class JavaFXController implements Controller {
    private JavaFXAppInfo info;
    private final List<EditorShape> shapes = new ArrayList<>();
    private final Map<EditorShape, Node> shapesToNative = new IdentityHashMap<>();
    private final Map<Node, EditorShape> nativesToShape = new IdentityHashMap<>();
    private Color backgroundColor;
    private Pane canvas;
    private double canvasStartY;
    private final Group mainGroup = new Group();
    private Consumer<MouseEvent> onCanvasClickCallback;
    private Consumer<MouseEvent> onCanvasMousePressedCallback;
    private Consumer<MouseEvent> onCanvasMouseReleasedCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeClickCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeMousePressCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeMouseReleasedCallback;
    private Consumer<MouseEvent> onDrag;
    private Rectangle selectedBox;
    public Group getMainGroup(){
        return this.mainGroup;
    }
    @Override
    public void launch(String windowName, int windowWidth, int windowHeight, Runnable init) {
        App.controller = this;
        App.windowName = windowName;
        App.windowWidth = windowWidth;
        App.windowHeight = windowHeight;
        App.init = info -> {
            this.info = info;
            init.run();
        };
        Application.launch(App.class);
    }

    @Override
    public void addButton(double x, double y, String text, Runnable callback) {
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setOnAction(e -> callback.run());
        this.mainGroup.getChildren().add(button);
    }

    @Override
    public void createCanvas(double x, double y, double width, double height, java.awt.Color backgroundColor) {
        this.canvasStartY = y;
        this.backgroundColor = JavaFXUtil.convertAWTToJavaFXColor(backgroundColor);
        this.canvas = new Pane();
        this.canvas.setLayoutX(x);
        this.canvas.setLayoutY(y);
        this.canvas.setPrefWidth(width);
        this.canvas.setPrefHeight(height);
        this.mainGroup.getChildren().add(this.canvas);
        this.onCanvasClickCallback = e -> {};
        this.onCanvasMousePressedCallback = e -> {};
        this.onCanvasMouseReleasedCallback = e -> {};
        this.onShapeClickCallback = (s, e) -> {};
        this.onShapeMousePressCallback = (s, e) -> {};
        this.canvas.setOnMouseClicked(e -> {
            MouseButtonType button = getButtonFromNativeButton(e.getButton());
            if(button == null) return;
            MouseEvent mouseEvent = new MouseEvent(button, e.getX(), e.getY());
            Node touched = nativesToShape.keySet()
                    .stream()
                    .filter(l -> l.contains(e.getX(), e.getY()))
                    .findFirst()
                    .orElse(null);
            if(touched != null) {
                onShapeClickCallback.accept(nativesToShape.get(touched), mouseEvent);
            } else {
                onCanvasClickCallback.accept(mouseEvent);
            }
        });
        this.canvas.setOnMousePressed(e -> onMouseEventFunction(onShapeMousePressCallback, onCanvasMousePressedCallback, e));
        this.canvas.setOnMouseReleased(e -> onMouseEventFunction(onShapeMouseReleasedCallback, onCanvasMouseReleasedCallback, e));
        this.canvas.setOnMouseDragged(e->{
            MouseButtonType button = getButtonFromNativeButton(e.getButton());
            if(button == null) return;
            MouseEvent mouseEvent = new MouseEvent(button, e.getX(), e.getY());
            onDrag.accept(mouseEvent); 
        });
    }
    private void onMouseEventFunction(BiConsumer<EditorShape, MouseEvent> shapeConsumer, Consumer<MouseEvent> canvasConsumer, javafx.scene.input.MouseEvent event) {
        MouseButtonType button = getButtonFromNativeButton(event.getButton());
        if(button == null) return;
        MouseEvent mouseEvent = new MouseEvent(button, event.getX(), event.getY());
        nativesToShape.keySet()
                .stream()
                .filter(l -> l.contains(event.getX(), event.getY()))
                .findFirst()
                .ifPresentOrElse(
                        touched -> shapeConsumer.accept(nativesToShape.get(touched), mouseEvent),
                        () -> canvasConsumer.accept(mouseEvent));
    }

    @Override
    public void addFixedRectangle(FixedRectangle fixedRectangle) {
        Rectangle rectangle = new Rectangle(
                fixedRectangle.getX() + this.canvas.getLayoutX(),
                fixedRectangle.getY() + this.canvas.getLayoutY(),
                Math.min(this.canvas.getPrefWidth(), fixedRectangle.getWidth()),
                Math.min(this.canvas.getPrefHeight(), fixedRectangle.getHeight()));
        rectangle.setFill(JavaFXUtil.convertAWTToJavaFXColor(fixedRectangle.getColor()));
        this.mainGroup.getChildren().add(rectangle);
    }

    @Override
    public void addPNGImage(double x, double y, double width, double height, InputStream inputStream) {
        ImageView imageView = new ImageView(new Image(inputStream));
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        this.canvas.getChildren().add(0, imageView);
    }

    @Override
    public void addShape(EditorShape shape) {
        JavaFXShapeRenderer builder = new JavaFXShapeRenderer();
        shape.createNativeShape(builder);
        this.shapes.add(shape);
        Node newNativeShape = builder.getGroup();
        this.canvas.getChildren().add(newNativeShape);
        this.shapesToNative.put(shape, newNativeShape);
        this.nativesToShape.put(newNativeShape, shape);
    }

    @Override
    public void update(EditorShape shape) {
        if(!this.shapes.contains(shape)) throw new IllegalArgumentException();
        JavaFXShapeRenderer builder = new JavaFXShapeRenderer();
        shape.createNativeShape(builder);
        Node removedNative = this.shapesToNative.remove(shape);
        this.nativesToShape.remove(removedNative);
        this.canvas.getChildren().remove(removedNative);
        Node newNativeShape = builder.getGroup();
        this.canvas.getChildren().add(newNativeShape);
        this.shapesToNative.put(shape, newNativeShape);
        this.nativesToShape.put(newNativeShape, shape);
    }

    @Override
    public void removeShape(EditorShape shape) {
        if(!this.shapes.contains(shape)) throw new IllegalArgumentException();
        Node removedNative = this.shapesToNative.remove(shape);
        this.nativesToShape.remove(removedNative);
        this.canvas.getChildren().remove(removedNative);
        this.shapes.remove(shape);
    }

    @Override
    public void removeAllShapesExcept(List<EditorShape> shapes) {
        List.copyOf(this.shapes)
                .stream()
                .filter(s -> !shapes.contains(s))
                .forEach(this::removeShape);
    }

    @Override
    public void redraw() {
        this.canvas.setBackground(new Background(new BackgroundFill(this.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
        for(EditorShape shape : this.shapes) {
            update(shape);
        }
    }

    @Override
    public void clearCanvas() {
        this.canvas.getChildren().clear();
        this.shapesToNative.clear();
        this.nativesToShape.clear();
        this.shapes.clear();
        this.canvas.setBackground(new Background(new BackgroundFill(this.backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    @Override
    public void setSelectBox(Area area) {
        this.canvas.getChildren().remove(this.selectedBox);
        if(area == null) {
            this.selectedBox = null;
        } else {
            java.awt.Rectangle rectangle = area.toRectangle();
            this.selectedBox = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            this.selectedBox.setFill(Color.TRANSPARENT);
            this.selectedBox.setStroke(Color.BLACK);
            this.selectedBox.setStrokeLineJoin(StrokeLineJoin.ROUND);
            this.selectedBox.setStrokeDashOffset(10);
            this.selectedBox.getStrokeDashArray().setAll(10.0, 10.0);
            this.selectedBox.setMouseTransparent(true);
            this.canvas.getChildren().add(this.selectedBox);
        }
    }

    @Override
    public void setOnCanvasClick(Consumer<MouseEvent> callback) {
        this.onCanvasClickCallback = callback;
    }

    @Override
    public void setOnCanvasMousePressed(Consumer<MouseEvent> callback) {
        this.onCanvasMousePressedCallback = callback;
    }

    @Override
    public void setOnCanvasMouseReleased(Consumer<MouseEvent> callback) {
        this.onCanvasMouseReleasedCallback = callback;
    }

    @Override
    public void setOnShapeClick(BiConsumer<EditorShape, MouseEvent> callback) {
        this.onShapeClickCallback = callback;
    }
    private MouseButtonType getButtonFromNativeButton(MouseButton button) {
        switch (button) {
            case PRIMARY: return MouseButtonType.PRIMARY;
            case SECONDARY: return MouseButtonType.SECONDARY;
            case MIDDLE: return MouseButtonType.MIDDLE;
            default: return null;
        }
    }

    @Override
    public void setOnShapeMousePressed(BiConsumer<EditorShape, MouseEvent> callback) {
        this.onShapeMousePressCallback = callback;
    }

    @Override
    public void setOnShapeMouseReleased(BiConsumer<EditorShape, MouseEvent> callback) {
        this.onShapeMouseReleasedCallback = callback;
    }

    @Override
    public void setOnDrag(Consumer<MouseEvent> callback) {
       this.onDrag = callback;
    }

    @Override
    public void setOnShutdown(Runnable callback) {
        this.info.getStage().setOnCloseRequest(e -> callback.run());
    }

    @Override
    public Stream<EditorShape> shapes() {
        return shapes.stream();
    }

    @Override
    public ContextMenuBuilder getContextMenuBuilder() {
        return new JavaFXContextMenuBuilder(this.info, (int) this.canvasStartY);
    }

    @Override
    public XShapeFileDialogFactory getFileDialogFactory() {
        return new XShapeFileDialogFactoryFX(this.info.getStage());
    }

    @Override
    public MenuBuilder getMenuBuilder() {
        return new MenuBuilderFX(this::redraw);
    }
    @Override
    public void addButtonWithIcon(double x, double y,double width, double height, InputStream inputStream, Runnable callback) {
        ImageView imageview= new ImageView(new Image(inputStream));
        imageview.setFitHeight(height);
        imageview.setFitWidth(width);
        Button button = new Button("", imageview);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setOnAction(e -> callback.run());
        this.mainGroup.getChildren().add(button);
    }

    @Override
    public void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
    }
}
