package ru.alkoleft.bsl.doc.render.processor;

import java.nio.file.Path;

public class AppendProcessor extends OutputProcessor {
  @Override
  public boolean needRender(Path location) {
    return !manualContent.contains(location);
  }
}
