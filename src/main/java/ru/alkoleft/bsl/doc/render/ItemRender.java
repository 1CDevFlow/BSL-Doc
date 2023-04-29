package ru.alkoleft.bsl.doc.render;

import java.io.IOException;
import java.nio.file.Path;

public interface ItemRender {
  void put(String key, Object value);

  void renderToFile(Path fileName) throws IOException;
}
