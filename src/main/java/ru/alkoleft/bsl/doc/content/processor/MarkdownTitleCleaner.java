package ru.alkoleft.bsl.doc.content.processor;

import java.util.stream.Collectors;

public class MarkdownTitleCleaner implements TitleCleaner {
  @Override
  public String cleanTitle(String content) {
    return content.lines()
        .filter(it -> !it.startsWith("# "))
        .collect(Collectors.joining("\n"));
  }
}
