package ru.alkoleft.bsl.doc.content.processor;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Path;

public interface TitleProcessor {
  static TitleProcessor getInstance() {
    return Factory.instance;
  }

  String cleanTitle(String content);

  String getTitle(String content);

  String getTitle(Path path);

  @UtilityClass
  class Factory {
    TitleProcessor instance;

    public TitleProcessor create(OutputFormat format) {
      return instance = new MarkdownTitleProcessor();
    }
  }
}
