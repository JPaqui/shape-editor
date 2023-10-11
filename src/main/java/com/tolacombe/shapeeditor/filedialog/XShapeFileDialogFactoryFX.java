package com.tolacombe.shapeeditor.filedialog;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import javafx.stage.Stage;

public class XShapeFileDialogFactoryFX implements XShapeFileDialogFactory {
    Stage stage;
    public XShapeFileDialogFactoryFX(Stage stage){
        this.stage=stage;
    }

    @Override
    public XShapeFileDialog createLoadFileDialog(String windowName, Path defaultFolder,
            List<String> allowedExtensions) {
        return new XShapeFileDialogFX(this.stage, FileDialog.LOAD, windowName, defaultFolder, allowedExtensions);
    }

    @Override
    public XShapeFileDialog createSaveFileDialog(String windowName, Path defaultFilePath,
            List<String> allowedExtensions) {
        return new XShapeFileDialogFX(this.stage, FileDialog.SAVE, windowName, defaultFilePath, allowedExtensions);
    }
    
}
