package com.tolacombe.shapeeditor.filedialog;

import java.nio.file.Path;
import java.util.List;

public interface XShapeFileDialogFactory {
    XShapeFileDialog createLoadFileDialog(String windowName, Path defaultFolder, List<String> allowedExtensions);
    XShapeFileDialog createSaveFileDialog(String windowName, Path defaultFilePath, List<String> allowedExtensions);
  }