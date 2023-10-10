package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;
import ru.alkoleft.bsl.doc.render.contexts.ContextFactory;
import ru.alkoleft.bsl.doc.render.handlebars.RenderContext;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Render {
  RenderContext renderContext;
  public Render(RenderContext renderContext) {
    this.renderContext = renderContext;
  }

  @SneakyThrows
  public void render(ModuleInfo module, Path outputPath, int index) {
    log.debug("Render module '{}' to '{}'", module.getOwner().getName(), outputPath);

    var context = ContextFactory.create(module, index);
    renderContext.renderToFile("module", context, outputPath);
  }

  @SneakyThrows
  public void render(MDSubsystem subsystem, Path path, int level, List<String> childrenItems) {
    log.debug("Render subsystem '{}' to '{}'", subsystem.getName(), path);

    var context = ContextFactory.create(subsystem, childrenItems, 0, level);
    renderContext.renderToFile("subsystem", context, path);
  }
}
