package ru.alkoleft.bsl.doc.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.options.MergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Path;
import java.util.List;

class RenderCommandTest {

  RenderCommand cmd;

  @BeforeEach
  void setUp() {
    cmd = new RenderCommand();
    cmd.sources = Path.of("/home/common/develop/repos/open-source/bsldoc/src/test/resources/configuration");
    cmd.destination = Path.of("/tmp/bsl-doc-fixture");
    cmd.format = OutputFormat.ConfluenceMarkdown;
    cmd.onlySubsystems = List.of("ППИ");
    cmd.regions = List.of(RegionSymbol.PUBLIC_REGION_RU);
    cmd.manualDocumentation = Path.of("/home/common/develop/repos/open-source/bsldoc/src/test/resources/docs");
    cmd.mergeStrategy = MergeStrategy.MERGE;
  }

  @Test
  void run() {
    cmd.run();
  }
}