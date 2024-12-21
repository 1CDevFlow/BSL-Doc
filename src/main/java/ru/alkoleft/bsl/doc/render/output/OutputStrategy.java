package ru.alkoleft.bsl.doc.render.output;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.helpers.Strings;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.Page;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Files;
import java.nio.file.Path;

@Setter
@Getter
public class OutputStrategy {
  protected OutputFormat format;
  protected ManualContent manualContent;
  protected ContentModel contentModel;

  public static OutputStrategy create(ManualMergeStrategy strategy) {
    return switch (strategy) {
      case APPEND -> new AppendStrategy();
      case MERGE -> new MergeStrategy();
      default -> new OutputStrategy();
    };
  }

  public boolean needRender(Path location) {
    return true;
  }

  protected Page addToContentModel(Path path, String content) {
    var page = getContentModel().append(path);

    if (Strings.isNullOrEmpty(page.getTitle())) {
      var title = TitleProcessor.getInstance().getTitle(content);
      page.setTitle(title);
    }

    return page;
  }

  public void init(OutputFormat format, ManualContent manualContent, ContentModel contentModel) {
    this.format = format;
    this.manualContent = manualContent;
    this.contentModel = contentModel;
  }

  @SneakyThrows
  public Page save(Path itemPath, String content) {
    Files.createDirectories(itemPath.getParent());
    Files.writeString(itemPath, content);
    return addToContentModel(itemPath, content);
  }
}
