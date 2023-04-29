package ru.alkoleft.bsl.doc.commands;

import lombok.SneakyThrows;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ru.alkoleft.bsl.doc.render.Render;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.render.Factory;
import ru.alkoleft.bsl.doc.render.OutputFormat;
import ru.alkoleft.bsl.doc.render.RenderOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Command(helpCommand = true)
public class RenderCommand implements Runnable {
  @Parameters(description = "source")
  Path sources;
  @Parameters(description = "destination")
  Path destination;

  @Option(names = {"-f", "--format"}, defaultValue = "Markdown")
  OutputFormat format;
  @Option(names = {"-s", "--only-subsystems"})
  List<String> onlySubsystems;

  @Option(names = {"-r", "--regions"}, split = " ", defaultValue = RegionSymbol.PUBLIC_REGION_RU + " " + RegionSymbol.PUBLIC_REGION_EN)
  List<String> regions;

  @SneakyThrows
  @Override
  public void run() {

    var filter = Filter.builder()
        .isExport(true);
    regions.forEach(filter::region);

    var options = RenderOptions.builder()
        .outputFormat(format)
        .subsystemHierarchy(true);
    onlySubsystems.forEach(options::rootSubsystem);

    var renderContext = Factory.createRenderContext(options.build());
    var render = new Render(renderContext);

    BslContext bslContext = new BslContext(sources, filter.build());
    bslContext.load();

    Files.createDirectories(destination);
    render.render(bslContext, destination);
  }
}
