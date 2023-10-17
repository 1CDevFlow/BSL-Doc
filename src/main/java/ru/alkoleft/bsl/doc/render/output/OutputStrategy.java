package ru.alkoleft.bsl.doc.render.output;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class OutputStrategy {
  @Setter
  protected OutputFormat format;
  @Setter
  protected ManualContent manualContent;

  public boolean needRender(Path location) {
    return true;
  }

  public static OutputStrategy create(ManualMergeStrategy strategy) {
    OutputStrategy processor;
    switch (strategy) {
      case APPEND:
        processor = new AppendStrategy();
        break;
      case MERGE:
        processor = new MergeStrategy();
        break;
      default:
        processor = new OutputStrategy();
        break;
    }
    return processor;
  }

  public void init(OutputFormat format, ManualContent manualContent) {
    this.format = format;
    this.manualContent = manualContent;
  }

  @SneakyThrows
  public void save(Path itemPath, String content) {
    Files.createDirectories(itemPath.getParent());
    Files.writeString(itemPath, content);
  }
}
