package ru.alkoleft.bsl.doc.commands;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.options.ChildLayout;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Files;
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

  @SneakyThrows
  @Test
  void runFromFile(@TempDir Path tempDir) {
    var config = String.format(
        "{\"subsystems\": [\"ППИ\"]," +
        "\"childLayout\": \"SUB_DIRECTORY\"," +
        "\"manualDocs\": \"%s\"," +
        "\"mergeStrategy\": \"MERGE\"," +
        "\"format\": \"ConfluenceMarkdown\"," +
        "\"header\": {\"content\": \"__{{present}}__\\n\"}}", getResource("docs"));
    var configPath = tempDir.resolve("config.json");
    Files.writeString(configPath, config);

    RenderCommand.builder()
        .sources(getResource("configuration"))
        .destination(tempDir.resolve("bsl-doc-fixture"))
        .optionsFile(configPath)
        .build()
        .run();
  }

  @SneakyThrows
  private Path getResource(String name) {
    return Path.of(getClass().getClassLoader().getResource(name).toURI());
  }
}