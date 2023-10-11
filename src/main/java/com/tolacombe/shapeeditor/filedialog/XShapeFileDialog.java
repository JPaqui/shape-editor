package com.tolacombe.shapeeditor.filedialog;

import java.nio.file.Path;

public interface XShapeFileDialog {
      /**
   * Asks the user wherewhich file to use, return null if it fails.
   * @return the file or null
   */
    Path request();
}
