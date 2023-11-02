package ru.alkoleft.bsl.doc.render;

import com.github.jknack.handlebars.Context;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.render.handlebars.RenderContext;

import java.nio.file.Path;

@UtilityClass
public class BaseRender {
  @Getter
  RenderContext renderContext;

  public void setContext(RenderContext renderContext) {
    BaseRender.renderContext = renderContext;
  }

  public void renderToFile(String templateName, Context context, Path output) {
    renderContext.renderToFile(templateName, context, output);
  }

  public String render(String templateName, Context context) {
    return renderContext.render(templateName, context);
  }
}
