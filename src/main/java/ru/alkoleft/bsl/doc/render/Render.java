package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import com.github._1c_syntax.mdclasses.mdo.MDCommonModule;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.ModuleContext;

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
  public void render(ModuleContext module, Path outputPath, int index) {
    var itemRender = renderContext.getRender("module");

    log.debug("Render module '{}' to '{}'", module.getOwner().getName(), outputPath);

    itemRender.put("index", index);
    itemRender.put("name", module.getOwner().getName());
    itemRender.put("present", getPresent(module.getOwner()));
    itemRender.put("isCommonModule", module.getOwner() instanceof MDCommonModule);
    itemRender.put("type", module.getOwner().getMdoType().getNameRu());
    itemRender.put("methods", module.getMethods());
    itemRender.put("description", module.getDescription());
    itemRender.renderToFile(outputPath);
  }

  private String getPresent(AbstractMDObjectBase object) {
    if (object.getSynonyms().isEmpty()) {
      return object.getName();
    } else {
      return object.getSynonyms().get(0).getContent();
    }
  }

  public void render(ModuleContext module, StructureStrategy strategy, int index) {
    var path = strategy.getModulePath(module);
    render(module, path, index);
  }

  @SneakyThrows
  public void render(MDSubsystem subsystem, Path path, int level, List<String> childrenItems) {
    var itemRender = renderContext.getRender("subsystem");
    log.debug("Render module '{}' to '{}'", subsystem.getName(), path);

    itemRender.put("level", level);
    itemRender.put("name", subsystem.getName());
    itemRender.put("present", getPresent(subsystem));
    itemRender.put("description", subsystem.getComment());
    itemRender.put("children", childrenItems);
    itemRender.renderToFile(path);
  }

  public void render(MDSubsystem subsystem, StructureStrategy strategy, int level) {
    Path path;
    if (level == 0) {
      path = strategy.getPath().resolve("index.md");
    } else {
      path = strategy.getObjectPath(subsystem).resolve("index.md");
    }
    render(subsystem, path, level, Collections.emptyList());
  }
}
