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
import ru.alkoleft.bsl.doc.options.ChildLayout;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputFormat;
import ru.alkoleft.bsl.doc.options.OutputHierarchy;
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

  @Option(names = {"-f", "--format"})
  private OutputFormat format;

  @Option(names = {"-h", "--hierarchy"})
  private OutputHierarchy hierarchy;

  @Option(names = {"-s", "--only-subsystems"})
  private List<String> onlySubsystems;

  @Option(names = {"-r", "--regions"}, split = " ")
  private List<String> regions;

  @Option(names = {"-m", "--manual-docs"}, description = "Path to manual documentations")
  private Path manualDocumentation;

  @Option(names = {"-ms", "--merge-strategy"}, description = "Merge strategy for manual and generated documentation")
  private ManualMergeStrategy manualMergeStrategy;

  @Option(names = {"-cl", "--child-layout"}, description = "Child pages layout")
  private ChildLayout childLayout;

  @Option(names = {"--config"}, description = "Options file")
  private Path optionsFile;

  RenderCommandOptions options() {
    RenderCommandOptions options;
    if (optionsFile != null) {
      options = RenderCommandOptions.load(optionsFile);
    } else {
      options = new RenderCommandOptions();
    }

    if (format != null) {
      options.setFormat(format);
    }

    if (onlySubsystems != null) {
      options.setSubsystems(onlySubsystems);
    }

    if (regions != null) {
      options.setRegions(regions);
    }

    if (manualDocumentation != null) {
      options.setManualDocs(manualDocumentation);
    }

    if (manualMergeStrategy != null) {
      options.setMergeStrategy(manualMergeStrategy);
    }

    if (childLayout != null) {
      options.setChildLayout(childLayout);
    }

    if (hierarchy != null) {
      options.setHierarchy(hierarchy);
    }

    return options;
  }

  @SneakyThrows
  @Override
  public void run() {

    var commandOptions = options();

    var filterBuilder = Filter.builder()
        .isExport(true);
    commandOptions.getRegions().forEach(filterBuilder::region);
    commandOptions.getSubsystems().forEach(filterBuilder::rootSubsystem);

    var optionsBuilder = OutputOptions.builder()
        .outputFormat(commandOptions.getFormat())
        .hierarchy(commandOptions.getHierarchy())
        .childLayout(commandOptions.getChildLayout());

    var filter = filterBuilder.build();
    var options = optionsBuilder.build();

    log.debug("Filter: " + filter.toString());
    log.debug("Options: " + options.toString());
    log.debug("Sources: " + sources);
    log.debug("Manual: " + commandOptions.getManualDocs());
    log.debug("Output: " + destination);
    log.debug("Merge manual: " + commandOptions.getMergeStrategy());

    var manager = AutodocManager.builder()
        .filter(filter)
        .outputOptions(options)
        .manualDocumentation(commandOptions.getManualDocs())
        .manualMergeStrategy(commandOptions.getMergeStrategy())
        .destination(destination)
        .sources(sources)
        .header(commandOptions.getHeaderContent())
        .footer(commandOptions.getFooterContent())
        .build();

    manager.loadData();
    manager.clearOutput();
    manager.generateDocumentation();
  }
}
