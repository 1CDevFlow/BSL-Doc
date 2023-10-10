package ru.alkoleft.bsl.doc.render.processor;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.options.MergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;
import ru.alkoleft.bsl.doc.render.Render;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class OutputProcessor {
  @Setter
  protected Render render;
  @Setter
  protected OutputFormat format;
  @Setter
  protected ManualContent manualContent;

  public boolean needRender(Path location) {
    return true;
  }

  @SneakyThrows
  public void save(Path itemPath, String content) {
    Files.writeString(itemPath, content);
  }

  public void init(Render render, OutputFormat format, ManualContent manualContent) {
    this.render = render;
    this.format = format;
    this.manualContent = manualContent;
  }

  public static class Factory {
    public static OutputProcessor create(MergeStrategy strategy) {
      OutputProcessor processor;
      switch (strategy) {
        case APPEND:
          processor = new AppendProcessor();
          break;
        case MERGE:
          processor = new MergeProcessor();
          break;
        default:
          processor = new OutputProcessor();
          break;
      }
      return processor;
    }
  }
}
