package ru.alkoleft.bsl.doc.render.output;

import java.nio.file.Path;

public class AppendStrategy extends OutputStrategy {
  @Override
  public boolean needRender(Path location) {
    return !manualContent.contains(location);
  }
}
