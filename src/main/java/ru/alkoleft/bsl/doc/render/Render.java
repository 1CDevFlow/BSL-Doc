package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.ModuleContext;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class Render {

  RenderContext renderContext;

  public Render(RenderContext renderContext) {
    this.renderContext = renderContext;
  }

  public void render(BslContext bslContext, Path output) {
    AtomicInteger index = new AtomicInteger();

    renderContext.setContext(bslContext);
    bslContext.getModules().forEach(it -> renderModule(it, output, index.getAndIncrement()));
  }

  @SneakyThrows
  public void renderModule(ModuleContext module, Path outputPath, int index) {
    var itemRender = renderContext.getRender("module");

    itemRender.put("index", index);
    itemRender.put("name", module.getOwner().getName());
    itemRender.put("present", getPresent(module.getOwner()));
    itemRender.put("methods", module.getMethods());
    itemRender.put("description", module.getDescription());
    itemRender.renderToFile(outputPath.resolve(module.getOwner().getName() + ".md"));
  }

  private String getPresent(AbstractMDObjectBSL object) {
    if (object.getSynonyms().isEmpty()) {
      return object.getName();
    } else {
      return object.getSynonyms().get(0).getContent();
    }
  }
}
