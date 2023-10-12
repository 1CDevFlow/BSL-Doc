package ru.alkoleft.bsl.doc.model;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.indexer.FileIndexer;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Path;

@UtilityClass
public class ContentModelBuilder {
  public ContentModel build(Path location, OutputFormat format) {
    var model = new ContentModel();

    try (var files = new FileIndexer(format.getExtension()).pagePaths(location)) {
      files.map(it -> new Page(it, it.getFileName().getName(0).toString(), PageType.MANUAL))
          .peek(it -> it.setTitle(TitleProcessor.getInstance().getTitle(it.getPath())))
          .forEach(model.getPages()::add);
    }

    return model;
  }
}
