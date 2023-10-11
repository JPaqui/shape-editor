package com.tolacombe.shapeeditor.filedialog;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.awt.FileDialog;
import java.awt.Frame;
import java.util.List;

public class XShapeFileDialogAWT implements XShapeFileDialog {
    private final FileDialog fileDialog;

    public XShapeFileDialogAWT(Frame frame, int mode, String windowName, Path defaultFolder, List<String> allowedExtensions) {
        this.fileDialog = new FileDialog(frame, windowName, mode);
        this.fileDialog.setDirectory(defaultFolder.toString());
        this.fileDialog.setFilenameFilter(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return allowedExtensions.stream()
                        .map(ex -> ex.replace("*", "").replace(".", "").toLowerCase())
                        .anyMatch(ex -> name.endsWith("." + ex));
            }
        });
        this.fileDialog.setFile("Sauvegarde." + allowedExtensions.stream().findFirst().orElse("txt"));
    }

    @Override
    public Path request() {
        fileDialog.setVisible(true);
        return this.fileDialog.getFile() == null ? null : Path.of(this.fileDialog.getDirectory(), this.fileDialog.getFile());
    }
}
