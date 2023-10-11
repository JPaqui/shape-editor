package com.tolacombe.shapeeditor.filedialog;

import java.awt.FileDialog;
import java.awt.Frame;
import java.nio.file.Path;
import java.util.List;

public class XShapeFileDialogFactoryAWT implements XShapeFileDialogFactory{
    Frame frame;
    public XShapeFileDialogFactoryAWT(Frame frame){
        this.frame=frame;
    }

    @Override
    public XShapeFileDialog createLoadFileDialog(String windowName, Path defaultFolder,
            List<String> allowedExtensions) {
        return new XShapeFileDialogAWT( this.frame, FileDialog.LOAD, windowName,defaultFolder,allowedExtensions);
    }

    @Override
    public XShapeFileDialog createSaveFileDialog(String windowName, Path defaultFilePath,
            List<String> allowedExtensions) {
        return new XShapeFileDialogAWT( this.frame, FileDialog.SAVE, windowName,defaultFilePath,allowedExtensions);
    }
    
}
