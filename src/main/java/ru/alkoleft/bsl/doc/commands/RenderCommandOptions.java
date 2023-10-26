package ru.alkoleft.bsl.doc.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.options.ChildLayout;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Data
public class RenderCommandOptions {
  private List<String> subsystems;
  private List<String> regions = List.of(RegionSymbol.PUBLIC_REGION_RU, RegionSymbol.PUBLIC_REGION_EN);
  private Path manualDocs;
  private ManualMergeStrategy mergeStrategy = ManualMergeStrategy.NONE;
  private ChildLayout childLayout = ChildLayout.SAME_DIRECTORY;
  private OutputFormat format;
  private PageBlock header;
  private PageBlock footer;

  @SneakyThrows
  public static RenderCommandOptions load(Path path) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(path.toFile(), RenderCommandOptions.class);
  }

  public String getHeaderContent() {
    if (header == null) {
      return null;
    } else {
      return header.getBlockContent();
    }
  }

  public String getFooterContent() {
    if (footer == null) {
      return null;
    } else {
      return footer.getBlockContent();
    }
  }

  @Getter
  public static class PageBlock {
    private Path path;
    private String content;

    @SneakyThrows
    public String getBlockContent() {
      if (content != null) {
        return content;
      } else if (path != null) {
        return Files.readString(path);
      } else {
        return null;
      }
    }
  }
}
