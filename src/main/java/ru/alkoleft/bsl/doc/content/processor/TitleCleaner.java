package ru.alkoleft.bsl.doc.content.processor;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.options.OutputFormat;

public interface TitleCleaner {
  String cleanTitle(String content);

  @UtilityClass
  class Factory {
    public TitleCleaner create(OutputFormat format) {
      return new MarkdownTitleCleaner();
    }
  }
}
