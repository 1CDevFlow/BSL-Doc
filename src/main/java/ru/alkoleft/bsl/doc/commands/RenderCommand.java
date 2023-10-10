package ru.alkoleft.bsl.doc.commands;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.bsl.symbols.RegionSymbol;
import ru.alkoleft.bsl.doc.render.OutputFormat;
import ru.alkoleft.bsl.doc.render.Render;
import ru.alkoleft.bsl.doc.render.RenderOptions;
import ru.alkoleft.bsl.doc.render.StructureRender;
import ru.alkoleft.bsl.doc.render.handlebars.RenderContext;
import ru.alkoleft.bsl.doc.structure.Builder;

import java.nio.file.Path;
import java.util.List;

@Slf4j
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

    var filterBuilder = Filter.builder()
        .isExport(true);
    regions.forEach(filterBuilder::region);
    onlySubsystems.forEach(filterBuilder::rootSubsystem);

    var optionsBuilder = RenderOptions.builder()
        .outputFormat(format)
        .subsystemHierarchy(true);

    var filter = filterBuilder.build();
    var options = optionsBuilder.build();

    log.debug("Filter: " + filter.toString());
    log.debug("Options: " + options.toString());

    var bslContext = new BslContext(sources, filter);
    var renderContext = RenderContext.Factory.create(options);

    var structure = Builder.build(bslContext, options);
    Builder.print(structure);

    new StructureRender(new Render(renderContext)).render(structure, destination);
  }
}
