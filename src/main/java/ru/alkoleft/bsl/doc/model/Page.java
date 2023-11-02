package ru.alkoleft.bsl.doc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class Page {
  private Path path;
  private String title;
  private PageType type;

  public String getTitle() {
    if (title != null) {
      return title;
    } else if (path == null) {
      return null;
    }

    return title = TitleProcessor.getInstance().getTitle(path);
  }
}
