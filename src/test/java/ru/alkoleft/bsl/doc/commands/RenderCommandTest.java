package ru.alkoleft.bsl.doc.commands;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Path;
import java.util.List;

class RenderCommandTest {

  @Test
  void run() {
    RenderCommand.builder()
        .sources(getResource("configuration"))
        .destination(Path.of("/tmp/bsl-doc-fixture"))
        .format(OutputFormat.ConfluenceMarkdown)
        .onlySubsystems(List.of("ППИ"))
        .regions(List.of(RegionSymbol.PUBLIC_REGION_RU))
        .manualDocumentation(getResource("docs"))
        .manualMergeStrategy(ManualMergeStrategy.MERGE)
        .build()
        .run();
  }

  @Test
  void runYaxUnit() {
    RenderCommand.builder()
        .sources(Path.of("/home/alko/develop/bia/orais/yaxunit/exts/yaxunit"))
        .destination(Path.of("/tmp/bsl-doc-yaxunit"))
        .format(OutputFormat.ConfluenceMarkdown)
        .onlySubsystems(List.of("ЮТДвижок"))
        .regions(List.of(RegionSymbol.PUBLIC_REGION_RU))
        .manualMergeStrategy(ManualMergeStrategy.MERGE)
        .build()
        .run();
  }

  @SneakyThrows
  private Path getResource(String name) {
    return Path.of(getClass().getClassLoader().getResource(name).toURI());
  }
}