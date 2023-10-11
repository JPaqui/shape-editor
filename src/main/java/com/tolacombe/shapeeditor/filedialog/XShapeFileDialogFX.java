package com.tolacombe.shapeeditor.filedialog;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class XShapeFileDialogFX implements XShapeFileDialog {
    private final FileChooser fileChooser;
    private final Stage stage;
    private final int mode;

    public XShapeFileDialogFX(Stage stage, int mode, String windowName, Path defaultFolder, List<String> allowedExtensions) {
        this.mode = mode;
        this.fileChooser = new FileChooser();
        this.fileChooser.setTitle(windowName);
        this.fileChooser.setInitialDirectory(defaultFolder.toFile());
        this.stage = stage;
        for (String extension : allowedExtensions) {
            extension = extension.replace("*", "").replace(".", "").toLowerCase();
            ExtensionFilter extensionFilter = new ExtensionFilter(extension, "*." + extension);
            this.fileChooser.getExtensionFilters().add(extensionFilter);
        }
    }

    @Override
    public Path request() {
        File file;
        switch (mode) {
            case FileDialog.SAVE: file = fileChooser.showSaveDialog(this.stage); break;
            case FileDialog.LOAD: file = fileChooser.showOpenDialog(this.stage); break;
            default: throw new IllegalStateException();
        }
        return file == null ? null : file.toPath();
    }
}
