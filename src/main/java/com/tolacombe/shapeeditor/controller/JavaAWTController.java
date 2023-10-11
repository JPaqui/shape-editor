package com.tolacombe.shapeeditor.controller;

import com.tolacombe.shapeeditor.contextmenu.AWTContextMenuBuilder;
import com.tolacombe.shapeeditor.contextmenu.ContextMenuBuilder;
import com.tolacombe.shapeeditor.event.MouseButtonType;
import com.tolacombe.shapeeditor.event.MouseEvent;
import com.tolacombe.shapeeditor.filedialog.XShapeFileDialogFactory;
import com.tolacombe.shapeeditor.filedialog.XShapeFileDialogFactoryAWT;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilder;
import com.tolacombe.shapeeditor.menubuilder.MenuBuilderAWT;
import com.tolacombe.shapeeditor.shape.EditorShape;
import com.tolacombe.shapeeditor.shaperenderer.awt.AWTShape;
import com.tolacombe.shapeeditor.shaperenderer.awt.JavaAWTShapeRenderer;
import com.tolacombe.shapeeditor.util.Area;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

class JavaAWTController implements Controller {
    private final List<EditorShape> shapes = new ArrayList<>();
    private final List<AWTShape> awtShapes = new ArrayList<>();
    private final Map<EditorShape, AWTShape> shapesToNative = new IdentityHashMap<>();
    private final Map<AWTShape, EditorShape> nativesToShape = new IdentityHashMap<>();
    private Color backgroundColor;
    private Canvas canvas;
    private Frame mainFrame;
    private Consumer<MouseEvent> onCanvasClickCallback;
    private Consumer<MouseEvent> onCanvasMousePressedCallback;
    private Consumer<MouseEvent> onCanvasMouseReleasedCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeClickCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeMousePressCallback;
    private BiConsumer<EditorShape, MouseEvent> onShapeMouseReleasedCallback;
    private Consumer<MouseEvent> onDrag;
    private Runnable onClose;
    private MouseButtonType currentLastMouseButtonDown = null;
    @Override
    public void launch(String windowsName, int windowWidth, int windowHeight, Runnable init) {
        this.mainFrame = new Frame(windowsName);
        this.onClose = () -> {};
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose.run();
                mainFrame.dispose();
            }
        });

        this.mainFrame.setVisible(true);
        this.mainFrame.setSize(
                windowWidth + mainFrame.getInsets().left + mainFrame.getInsets().right,
                windowHeight + mainFrame.getInsets().top + mainFrame.getInsets().bottom);
        init.run();
    }
    @Override
    public void addButton(double x, double y, String text, Runnable callback) {
        Button button = new Button(text);
        button.addActionListener(e -> callback.run());
        this.mainFrame.add(button);
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        this.mainFrame.validate();
        button.setLocation((int) x, (int) (mainFrame.getInsets().top + y));
        button.setSize(metrics.stringWidth(button.getLabel()) + 10, metrics.getHeight() + 10);
    }

    class Canvas extends Panel {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final List<FixedRectangle> fixedRectangles;
        private final List<Panel> backgroundObjects;
        private Rectangle selectionRectangle;
        private Color background;
        private BufferedImage buffer;

        public Canvas(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.fixedRectangles = new ArrayList<>();
            this.backgroundObjects = new ArrayList<>();
            setBounds(x, y, width, height);
        }
        public void setBackground(Color color) {
            this.background = color;
        }
        public void addFixedRectangle(FixedRectangle fixedRectangle) {
            this.fixedRectangles.add(fixedRectangle);
        }
        public void setSelectionRectangle(Area area) {
            this.selectionRectangle = area == null ? null : area.toRectangle();
        }
        public void addBackgroundObject(Panel panel) {
            this.backgroundObjects.add(panel);
        }
        @Override
        public void update(Graphics g) {
            if(buffer == null) {
                setBounds(x, y, width, height);
                buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);;
            }
            Graphics2D bufferGraphics = (Graphics2D) buffer.getGraphics();
            bufferGraphics.setColor(background);
            bufferGraphics.fillRect(0, 0, width, height);
            this.backgroundObjects.forEach(p -> p.paint(bufferGraphics));
            new ArrayList<>(awtShapes).forEach(s -> s.draw(bufferGraphics));
            bufferGraphics.setTransform(new AffineTransform());
            bufferGraphics.setColor(Color.BLACK);
            bufferGraphics.setStroke(new BasicStroke(1));
            this.fixedRectangles.forEach(r -> {
                bufferGraphics.setColor(r.getColor());
                bufferGraphics.fillRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            });
            if(selectionRectangle != null) {
                bufferGraphics.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1, new float[]{10, 10}, 0));
                bufferGraphics.setColor(Color.BLACK);
                bufferGraphics.drawRect(selectionRectangle.x, selectionRectangle.y, selectionRectangle.width, selectionRectangle.height);
            }
            g.drawImage(buffer, 0, 0, null);
        }
    }

    @Override
    public void createCanvas(double x, double y, double width, double height, Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        if(this.canvas != null) {
            this.mainFrame.remove(this.canvas);
        }
        this.canvas = new Canvas((int) x + mainFrame.getInsets().left, (int) (mainFrame.getInsets().top + y), (int) width, (int) height);
        this.mainFrame.add(this.canvas);
        this.canvas.setPreferredSize(new Dimension(100, 100));
        this.canvas.setBackground(this.backgroundColor);
        this.onCanvasClickCallback = e -> {};
        this.onCanvasMousePressedCallback = e -> {};
        this.onCanvasMouseReleasedCallback = e -> {};
        this.onShapeClickCallback = (s, e) -> {};
        this.onShapeMousePressCallback = (s, e) -> {};
        this.canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                MouseButtonType button = getButtonFromNativeButton(e.getButton());
                if(button == null) return;
                MouseEvent mouseEvent = new MouseEvent(button, e.getX(), e.getY());
                AWTShape touched = nativesToShape.keySet()
                        .stream()
                        .filter(l -> l.contains(e.getX(), e.getY()))
                        .findFirst()
                        .orElse(null);
                if(touched != null) {
                    onShapeClickCallback.accept(nativesToShape.get(touched), mouseEvent);
                } else {
                    onCanvasClickCallback.accept(mouseEvent);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                MouseButtonType button = getButtonFromNativeButton(e.getButton());
                if(button == null) return;
                currentLastMouseButtonDown = button;
                applyMouseEvent(onShapeMousePressCallback, onCanvasMousePressedCallback, button, e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                MouseButtonType button = getButtonFromNativeButton(e.getButton());
                if(button == null) return;
                if(currentLastMouseButtonDown == button) {
                    currentLastMouseButtonDown = null;
                }
                applyMouseEvent(onShapeMouseReleasedCallback, onCanvasMouseReleasedCallback, button, e);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {

            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {

            }
        });
        this.canvas.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                    MouseButtonType button = currentLastMouseButtonDown;
                    if(button == null) return;
                    MouseEvent mouseEvent = new MouseEvent(button, e.getX(), e.getY());
                    onDrag.accept(mouseEvent); 
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
            }
            
        });
        canvas.repaint();
    }
    private void applyMouseEvent(BiConsumer<EditorShape, MouseEvent> shapeConsumer, Consumer<MouseEvent> canvasConsumer, MouseButtonType button, java.awt.event.MouseEvent event) {
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
        this.canvas.addFixedRectangle(fixedRectangle);
    }

    @Override
    public void addPNGImage(double x, double y, double width, double height, InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            this.canvas.addBackgroundObject(new Panel() {
                @Override
                public void paint(Graphics g) {
                    g.drawImage(image, (int)x, (int)y, (int)width, (int)height, null);
                }
            });
            this.canvas.repaint();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void addShape(EditorShape shape) {
        JavaAWTShapeRenderer builder = new JavaAWTShapeRenderer();
        shape.createNativeShape(builder);
        this.shapes.add(shape);
        AWTShape newNativeShape = builder.getShapes();
        this.shapesToNative.put(shape, newNativeShape);
        this.nativesToShape.put(newNativeShape, shape);
        this.awtShapes.add(newNativeShape);
        this.canvas.repaint();
    }

    @Override
    public void update(EditorShape shape) {
        if(!this.shapes.contains(shape)) throw new IllegalArgumentException();
        JavaAWTShapeRenderer builder = new JavaAWTShapeRenderer();
        shape.createNativeShape(builder);
        AWTShape removedNative = this.shapesToNative.remove(shape);
        this.nativesToShape.remove(removedNative);
        this.awtShapes.remove(removedNative);
        AWTShape newNativeShape = builder.getShapes();
        this.shapesToNative.put(shape, newNativeShape);
        this.nativesToShape.put(newNativeShape, shape);
        this.awtShapes.add(newNativeShape);
        this.canvas.repaint();
    }

    @Override
    public void removeShape(EditorShape shape) {
        if(!this.shapes.contains(shape)) throw new IllegalArgumentException();
        AWTShape removedNative = this.shapesToNative.remove(shape);
        this.nativesToShape.remove(removedNative);
        this.awtShapes.remove(removedNative);
        this.shapes.remove(shape);
        this.canvas.repaint();
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
        for(EditorShape shape : this.shapes) {
            update(shape);
        }
    }

    @Override
    public void clearCanvas() {
        this.shapesToNative.clear();
        this.nativesToShape.clear();
        this.shapes.clear();
        this.awtShapes.clear();
        this.canvas.getGraphics().clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
    }

    @Override
    public void setSelectBox(Area area) {
        this.canvas.setSelectionRectangle(area);
        this.canvas.repaint();
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
    private MouseButtonType getButtonFromNativeButton(int button) {
        switch (button) {
            case java.awt.event.MouseEvent.BUTTON1: return MouseButtonType.PRIMARY;
            case java.awt.event.MouseEvent.BUTTON2: return MouseButtonType.MIDDLE;
            case java.awt.event.MouseEvent.BUTTON3: return MouseButtonType.SECONDARY;
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
        this.onDrag=callback;
    }

    @Override
    public void setOnShutdown(Runnable callback) {
        this.onClose = callback;
    }

    @Override
    public Stream<EditorShape> shapes() {
        return shapes.stream();
    }

    @Override
    public ContextMenuBuilder getContextMenuBuilder() {
        return new AWTContextMenuBuilder(this.canvas);
    }

    @Override
    public XShapeFileDialogFactory getFileDialogFactory() {
        return new XShapeFileDialogFactoryAWT(this.mainFrame);
    }

    @Override
    public MenuBuilder getMenuBuilder() {
        return new MenuBuilderAWT(this::redraw);
    }
    @Override
    public void addButtonWithIcon(double x, double y,double width, double height, InputStream inputStream, Runnable callback) {
        try {
            BufferedImage image = ImageIO.read(inputStream);
            Image img = image.getScaledInstance((int)width,(int)height,  java.awt.Image.SCALE_SMOOTH);
            ImageIcon imageIcon= new ImageIcon(img);
            JButton button = new JButton(imageIcon);
            button.addActionListener(e -> callback.run());
            this.mainFrame.add(button);
            this.mainFrame.validate();
            button.setLocation((int) x, (int) (mainFrame.getInsets().top + y));
            button.setSize((int) width+10, (int) height+10);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void alert(String message) {
        JOptionPane.showMessageDialog(null, message);
    }
}
