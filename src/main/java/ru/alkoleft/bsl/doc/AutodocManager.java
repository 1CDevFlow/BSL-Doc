package ru.alkoleft.bsl.doc;

import lombok.Builder;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.options.ManualMergeStrategy;
import ru.alkoleft.bsl.doc.options.OutputOptions;
import ru.alkoleft.bsl.doc.render.BaseRender;
import ru.alkoleft.bsl.doc.render.StructureRender;
import ru.alkoleft.bsl.doc.render.TemplatesDefinition;
import ru.alkoleft.bsl.doc.render.handlebars.RenderContext;
import ru.alkoleft.bsl.doc.render.output.OutputStrategy;
import ru.alkoleft.bsl.doc.structure.StructureBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class AutodocManager {

  private final OutputOptions outputOptions;
  private final Path destination;
  private final ManualMergeStrategy manualMergeStrategy;
  private final ManualContent manualContent;
  private final BslContext bslContext;
  private final TemplatesDefinition templatesDefinition;

  @Builder
  public AutodocManager(Filter filter,
                        OutputOptions outputOptions,
                        Path sources,
                        Path destination,
                        Path manualDocumentation,
                        ManualMergeStrategy manualMergeStrategy,
                        String header,
                        String footer) {
    this.destination = destination;
    this.outputOptions = outputOptions;
    this.manualMergeStrategy = manualMergeStrategy;
    this.templatesDefinition = new TemplatesDefinition(outputOptions.getOutputFormat().getPath(), header, footer);
    this.manualContent = new ManualContent(manualDocumentation, destination);
    this.bslContext = new BslContext(sources, filter);

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
    var contentModel = Objects.requireNonNullElseGet(manualContent.getContentModel(), ContentModel::new);
    processor.init(outputOptions.getOutputFormat(), manualContent, contentModel);
    var render = new StructureRender(outputOptions, processor, contentModel);

    BaseRender.setContext(RenderContext.Factory.create(templatesDefinition));
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
