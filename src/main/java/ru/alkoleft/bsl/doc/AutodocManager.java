package ru.alkoleft.bsl.doc;

import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputOptions;
import ru.alkoleft.bsl.doc.render.BaseRender;
import ru.alkoleft.bsl.doc.render.StructureRender;
import ru.alkoleft.bsl.doc.render.handlebars.RenderContext;
import ru.alkoleft.bsl.doc.render.output.OutputStrategy;
import ru.alkoleft.bsl.doc.structure.StructureBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AutodocManager {

  private final OutputOptions outputOptions;
  private final Path destination;
  private final ManualMergeStrategy manualMergeStrategy;
  private final ManualContent manualContent;
  private final BslContext bslContext;

  @Builder
  public AutodocManager(Filter filter, OutputOptions outputOptions, Path sources, Path destination, Path manualDocumentation, ManualMergeStrategy manualMergeStrategy) {
    this.destination = destination;
    this.outputOptions = outputOptions;
    this.manualMergeStrategy = manualMergeStrategy;
    manualContent = new ManualContent(manualDocumentation, destination);
    bslContext = new BslContext(sources, filter);

    TitleProcessor.Factory.create(outputOptions.getOutputFormat());
  }

  public void loadData() {
    manualContent.buildModel(outputOptions.getOutputFormat());
  }

  public void generateDocumentation() {
    manualContent.copy();

    var structure = StructureBuilder.builder(outputOptions).build(bslContext);
    StructureBuilder.print(structure);

    var processor = OutputStrategy.create(manualMergeStrategy);
    processor.init(outputOptions.getOutputFormat(), manualContent);
    var render = new StructureRender(outputOptions, processor, manualContent.getContentModel());

    BaseRender.setContext(RenderContext.Factory.create(outputOptions));
    render.render(structure, destination);
  }

  @SneakyThrows
  public void clearOutput() {
    if (Files.exists(destination)) {
      Files.walk(destination)
          .filter(it -> it != destination)
          .forEach(this::deletePath);
    }
  }

  private void deletePath(Path path) {
    try {
      PathUtils.delete(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
