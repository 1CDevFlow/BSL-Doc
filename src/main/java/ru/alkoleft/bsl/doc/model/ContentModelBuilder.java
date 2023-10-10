package ru.alkoleft.bsl.doc.model;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.indexer.FileIndexer;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class ContentModelBuilder {
  public ContentModel build(Path location, OutputFormat format) {
    var model = new ContentModel();

    try (var files = new FileIndexer(format.getExtension()).pagePaths(location)) {
      files.map(it -> new Page(it, it.getFileName().getName(0).toString(), PageType.UNKNOWN))
          .peek(it -> it.setTitle(extractTitle(it.getPath())))
          .forEach(model.getPages()::add);
    }

    return model;
  }

  @SneakyThrows
  private String extractTitle(Path path) {
    try (var lines = Files.lines(path)) {
      return lines
          .filter(it -> it.startsWith("# "))
          .findFirst()
          .map(it -> it.substring(2).trim()).orElse(null);
    }
  }
}
