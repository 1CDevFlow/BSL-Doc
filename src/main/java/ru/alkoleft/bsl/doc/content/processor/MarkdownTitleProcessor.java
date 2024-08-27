package ru.alkoleft.bsl.doc.content.processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class MarkdownTitleProcessor implements TitleProcessor {
  @Override
  public String cleanTitle(String content) {
    return content.lines()
        .filter(it -> !it.startsWith("# "))
        .collect(Collectors.joining("\n"));
  }

  @Override
  public String getTitle(String content) {
    return null;
  }

  @Override
  public String getTitle(Path path) {
    try (var lines = Files.lines(path)) {
      return lines
          .filter(it -> it.startsWith("# "))
          .findFirst()
          .map(it -> it.substring(2).trim()).orElse(null);
    } catch (IOException e) {
      // nothing
    }
    return "";
  }
}
