package com.tolacombe.shapeeditor;

import com.tolacombe.shapeeditor.action.Action;
import com.tolacombe.shapeeditor.action.ActionController;
import com.tolacombe.shapeeditor.action.SimpleAction;
import com.tolacombe.shapeeditor.action.State;
import com.tolacombe.shapeeditor.contextmenu.ContextMenuBuilder;
import com.tolacombe.shapeeditor.controller.Controller;
import com.tolacombe.shapeeditor.controller.FixedRectangle;
import com.tolacombe.shapeeditor.event.MouseButtonType;
import com.tolacombe.shapeeditor.event.MouseEvent;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.selection.SelectionController;
import com.tolacombe.shapeeditor.shape.*;
import com.tolacombe.shapeeditor.toolbar.Toolbar;
import com.tolacombe.shapeeditor.toolbar.ToolbarPutResult;
import com.tolacombe.shapeeditor.util.Area;
import com.tolacombe.shapeeditor.util.Util;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XShape {
    public static final String TOOLBAR_SAVE_FILE_NAME = ".xshape.toolbar";
    public static Path getToolbarSavePath() {
        return Util.getUserHome().resolve(TOOLBAR_SAVE_FILE_NAME);
    }
    private static final long DOUBLE_CLICK_INTERVAL_NANOS = 300_000_000; // 0.3s
    private boolean started;
    private Controller controller;
    private ActionController actionController;
    private Toolbar toolbar;
    private SelectionController selectionController;
    private int canvasStartX;
    private int canvasStartY;
    private int canvasWidth;
    private int canvasHeight;

    public void launch(Controller controller, String windowName, int windowWidth, int windowHeight, int canvasStartY) {
        if(this.started) throw new IllegalStateException();
        this.started = true;
        this.canvasStartX = 0;
        this.canvasStartY = canvasStartY;
        this.canvasWidth = windowWidth;
        this.canvasHeight = windowHeight - canvasStartY;
        this.controller = controller;
        controller.launch(windowName, windowWidth, windowHeight, this::init);
    }
    private final int toolbarWidth = 100;
    private Rectangle trashArea;

    private long lastPureClick;
    private double lastDragX;
    private double lastDragY;
    private EditorShape shape;

    private void init() {
        this.controller.createCanvas(canvasStartX, canvasStartY, canvasWidth, canvasHeight, Color.GRAY);
        this.controller.clearCanvas();
        this.toolbar = new Toolbar(toolbarWidth, 40, 2);
        this.controller.addFixedRectangle(new FixedRectangle(toolbarWidth, 0, 2, canvasHeight, Color.WHITE));
        this.trashArea = new Rectangle(canvasWidth - 45, canvasHeight - 45, 40, 40);
        final int buttonWidth = 30;
        final int buttonHeight = 30;
        this.controller.addButtonWithIcon(50, 0, buttonWidth, buttonHeight, getClass().getResourceAsStream("/save_icon.png"), this::onSave);
        this.controller.addButtonWithIcon(100, 0, buttonWidth, buttonHeight, getClass().getResourceAsStream("/load_icon.png"), this::onLoad);
        this.controller.addButtonWithIcon(150, 0, buttonWidth, buttonHeight, getClass().getResourceAsStream("/undo_icon.png"), this::onUndo);
        this.controller.addButtonWithIcon(200, 0, buttonWidth, buttonHeight, getClass().getResourceAsStream("/redo_icon.jpg"), this::onRedo);
        this.controller.addPNGImage(trashArea.x, trashArea.y, trashArea.width, trashArea.height, getClass().getResourceAsStream("/trash_icon.png"));

        this.selectionController = new SelectionController();

        this.onToolbarInit();
        this.controller.setOnShutdown(this::onToolbarSave);

        this.actionController = new ActionController(getCurrentCanvasState());


        this.controller.setOnCanvasClick(this::onClick);
        this.controller.setOnShapeClick((s, e) -> this.onClick(e));
        this.controller.setOnCanvasMousePressed(e -> {
            if(e.getButtonType() == MouseButtonType.SECONDARY) {
                ContextMenuBuilder contextMenuBuilder = this.controller.getContextMenuBuilder();
                List<EditorShape> shapes = findAllSelectedShapes().collect(Collectors.toList());
                if(shapes.size() > 1) {
                    contextMenuBuilder.addButton("Grouper", () -> group(shapes));
                }
                double x = e.getX();
                double y = e.getY();
                contextMenuBuilder.addButton("Ajouter rectangle par défaut", () -> createDefaultShape(new EditorRectangle(x, y, 200, 200, 0, Color.RED)));
                contextMenuBuilder.addButton("Ajouter cercle par défaut", () -> createDefaultShape(new EditorCircle(x, y, Color.pink, 50)));
                contextMenuBuilder.addButton("Ajouter polygone régulier par défaut", () -> createDefaultShape(new EditorRegularPolygon(x, y, 50, 5, 0, Color.YELLOW)));
                contextMenuBuilder.show(e.getX(), e.getY());
                return;
            }
            changeSelection(previous -> new Area((int)e.getX(), (int)e.getY(), (int)e.getX(), (int)e.getY()));
        });
        this.controller.setOnShapeMousePressed((s, e) ->{
            if(e.getButtonType() == MouseButtonType.SECONDARY) {
                List<EditorShape> shapes = findAllSelectedShapes().collect(Collectors.toList());
                ContextMenuBuilder contextMenuBuilder = this.controller.getContextMenuBuilder();
                if(shapes.size() > 1) {
                    contextMenuBuilder.addButton("Grouper", () -> group(shapes));
                }
                if(s instanceof EditorGroupShape) {
                    contextMenuBuilder.addButton("Dégrouper", () -> ungroup((EditorGroupShape)s));
                }
                contextMenuBuilder.addButton("Éditer", () -> edit(s));
                contextMenuBuilder.show(e.getX(), e.getY());
                return;
            }
            changeSelection(previous -> null);
            lastDragX = e.getX();
            lastDragY = e.getY();
            EditorShape newShape = toolbar.tryGetFromToolbar(s);
            if(newShape != null) {
                this.controller.addShape(newShape);
                this.shape = newShape;
            } else {
                this.shape = s;
            }
        });
        this.controller.setOnDrag((e) ->{
            if(shape != null) {
                double offsetX = e.getX() - lastDragX;
                double offsetY = e.getY() - lastDragY;
                lastDragX = e.getX();
                lastDragY = e.getY();
                shape.setX(shape.getX() + offsetX);
                shape.setY(shape.getY() + offsetY);
                this.controller.redraw();
            }
            changeSelection(previous -> {
                if(previous == null) return null;
                return previous.withX2((int)e.getX()).withY2((int)e.getY());
            });
        });
        this.controller.setOnShapeMouseReleased((s, e) ->{
            if(shape != null) {
                Area area = new Area(trashArea);
                if(area.intersects(shape)) {
                    this.controller.removeShape(this.shape);
                } else {
                    ToolbarPutResult result = toolbar.tryPutInToolbar(shape, e.getX(), e.getY());
                    if(result.getRemoved() != null) {
                        this.controller.removeShape(result.getRemoved());
                    }
                    if(result.getRemoved() != null || result.isShapePut()) {
                        this.controller.redraw();
                    }
                }
            }
            this.shape = null;
            this.actionController.saveAction(getCurrentCanvasState());
        });
        this.controller.setOnCanvasMouseReleased(e -> {
            if(selectionController.getArea() != null) {
                this.actionController.saveAction(getCurrentCanvasState());
            }
        });
    }
    // CONTEXT MENU
    private void onClick(MouseEvent event) {
        long now = System.nanoTime();
        if(now - this.lastPureClick < DOUBLE_CLICK_INTERVAL_NANOS) {
            this.onDoubleClick(event);
        }
        this.lastPureClick = now;
    }
    private void onDoubleClick(MouseEvent event) {
        changeSelection(previous -> new Area(toolbarWidth+1, 1, canvasWidth-2, canvasHeight-2));
        this.actionController.saveAction(getCurrentCanvasState());
    }
    private void changeSelection(Function<Area, Area> function) {
        Area area = this.selectionController.getArea();
        this.selectionController.setSelection(controller, function.apply(area));
    }
    private void group(List<EditorShape> shapes) {
        EditorGroupShape groupShape = new EditorGroupShape(shapes);
        shapes.forEach(this.controller::removeShape);
        this.controller.addShape(groupShape);
    }
    private void ungroup(EditorGroupShape shape) {
        List<EditorShape> shapes = shape.ungroup();
        this.controller.removeShape(shape);
        shapes.forEach(this.controller::addShape);
    }
    private void edit(EditorShape shape) {
        MenuBuilder menuBuilder = this.controller.getMenuBuilder();
        shape.populateMenu(menuBuilder, canvasWidth, canvasHeight);
        menuBuilder.display(() -> this.actionController.saveAction(getCurrentCanvasState()));
    }
    private void createDefaultShape(EditorShape shape) {
        this.controller.addShape(shape);
        this.actionController.saveAction(getCurrentCanvasState());
    }
    //END CONTEXT MENU
    private Action getCurrentCanvasState() {
        List<EditorShape> shapes = this.controller.shapes().collect(Collectors.toList());
        Map<EditorShape, Double> shapesMults = this.toolbar.getShapesMults();
        return new SimpleAction(shapes, shapesMults, this.selectionController.getArea());
    }
    private Stream<EditorShape> findAllSelectedShapes() {
        Area selection = this.selectionController.getArea();
        if(selection == null) {
            return Stream.empty();
        }
        return this.controller.shapes().filter(selection::contains);
    }
    private void onSave() {
        Path saveLocation = this.controller.getFileDialogFactory()
            .createSaveFileDialog("Choisissez un fichier à sauvegarder", Util.getUserHome(), List.of("xshape"))
            .request();
        if (saveLocation == null) return;

        try (var fos = Files.newOutputStream(saveLocation); var oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.controller.shapes().filter(this.toolbar::isNotInToolbarRange).collect(Collectors.toList()));
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
            this.controller.alert("Une erreur est arrivée lors de la sauvegarde du fichier.");
        }
    }
    private void onLoad() {
        Path loadLocation = this.controller.getFileDialogFactory()
            .createLoadFileDialog("Choisissez un fichier à charger", Util.getUserHome(), List.of("xshape"))
            .request();
        if (loadLocation == null) return;

        try (var fis = Files.newInputStream(loadLocation); var ois = new ObjectInputStream(fis)) {
            @SuppressWarnings("unchecked")
            List<EditorShape> shapes = (List<EditorShape>) ois.readObject();
            for (EditorShape editorShape : shapes) {
                if(this.toolbar.isInToolbarRange(editorShape)) {
                    throw new IOException("A loaded shape can't be in the toolbar");
                }
            }
            this.controller.removeAllShapesExcept(this.toolbar.allShapes().collect(Collectors.toList()));
            for (EditorShape editorShape : shapes) {
                this.controller.addShape(editorShape);
            }
            this.actionController.saveAction(getCurrentCanvasState());
        }catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            this.controller.alert("Une erreur est arrivée lors du chargement du fichier.");
        }
    }
    private void onToolbarSave() {
        Path toolbarSavePath = getToolbarSavePath();
        try (var fos = Files.newOutputStream(toolbarSavePath); var oos = new ObjectOutputStream(fos)) {
            oos.writeObject(this.toolbar.getShapesMults());
        } catch (IOException | ClassCastException e) {
            e.printStackTrace();
        }
    }
    private void onToolbarInit() {
        Path toolbarSavePath = getToolbarSavePath();
        if(!Files.isRegularFile(toolbarSavePath)) { // Doesn't exist, nothing to do
            return;
        }
        try (var fis = Files.newInputStream(toolbarSavePath); var ois = new ObjectInputStream(fis)) {
            @SuppressWarnings("unchecked")
            Map<EditorShape, Double> shapes = (Map<EditorShape, Double>) ois.readObject();
            int shapeSize = this.toolbar.getShapeSize();
            for(Map.Entry<EditorShape, Double> entry : shapes.entrySet()) {
                EditorShape tbShape = entry.getKey();
                if(this.toolbar.isNotInToolbarRange(tbShape)) {
                    throw new IOException("Illegal toolbar shape");
                }
                double tbWidth = tbShape.getWidth();
                double tbHeight = tbShape.getHeight();
                double size = Math.max(tbWidth, tbHeight);
                double mult = shapeSize / size;
                tbShape.setWidth(tbWidth * mult);
                tbShape.setHeight(tbHeight * mult);
                entry.setValue(entry.getValue() * mult);
            }
            this.toolbar.patch(shapes);
            for (EditorShape editorShape : shapes.keySet()) {
                this.controller.addShape(editorShape);
            }
        }catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            this.controller.alert("Une erreur est arrivée lors du chargement de la barre d'outils.");
        }
    }
    private State getState() {
        return new State(this.controller, this.toolbar, this.selectionController);
    }
    
    private void onUndo(){
        this.actionController.undo().ifPresent(action -> action.patch(getState()));
    }
    private void onRedo(){
        this.actionController.redo().ifPresent(action -> action.patch(getState()));
    }
}
