package ru.alkoleft.bsl.doc.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ru.alkoleft.bsl.doc.AutodocManager;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.options.ChildLayout;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;
import ru.alkoleft.bsl.doc.options.OutputOptions;

import java.nio.file.Path;
import java.util.List;

@Slf4j
@Command(helpCommand = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenderCommand implements Runnable {
  @Parameters(description = "source")
  private Path sources;
  @Parameters(description = "destination")
  private Path destination;
  @Option(names = {"-f", "--format"}, defaultValue = "Markdown")
  private OutputFormat format;
  @Option(names = {"-s", "--only-subsystems"})
  private List<String> onlySubsystems;
  @Option(names = {"-r", "--regions"}, split = " ", defaultValue = RegionSymbol.PUBLIC_REGION_RU + " " + RegionSymbol.PUBLIC_REGION_EN)
  private List<String> regions;
  @Option(names = {"-m", "--manual-docs"}, description = "Path to manual documentations")
  private Path manualDocumentation;
  @Option(names = {"-ms", "--merge-strategy"}, description = "Merge strategy for manual and generated documentation", defaultValue = "NONE")
  private ManualMergeStrategy manualMergeStrategy;
  @Option(names = {"-cl", "--child-layout"}, description = "Child pages layout", defaultValue = "SAME_DIRECTORY")
  private ChildLayout childLayout;

  @SneakyThrows
  @Override
  public void run() {

    var filterBuilder = Filter.builder()
        .isExport(true);
    regions.forEach(filterBuilder::region);
    onlySubsystems.forEach(filterBuilder::rootSubsystem);

    var optionsBuilder = OutputOptions.builder()
        .outputFormat(format)
        .subsystemHierarchy(true)
        .childLayout(childLayout);

    var filter = filterBuilder.build();
    var options = optionsBuilder.build();

    log.debug("Filter: " + filter.toString());
    log.debug("Options: " + options.toString());
    log.debug("Sources: " + sources);
    log.debug("Manual: " + manualDocumentation);
    log.debug("Output: " + destination);
    log.debug("Merge manual: " + manualMergeStrategy);

    var manager = AutodocManager.builder()
        .filter(filter)
        .outputOptions(options)
        .manualDocumentation(manualDocumentation)
        .manualMergeStrategy(manualMergeStrategy)
        .destination(destination)
        .sources(sources)
        .build();

    manager.loadData();
    manager.clearOutput();
    manager.generateDocumentation();
  }
}
