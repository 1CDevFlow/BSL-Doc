package ru.alkoleft.bsl.doc.render;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.render.handlebars.HandlebarsRenderContext;

@UtilityClass
public class Factory {
  public RenderContext createRenderContext(RenderOptions options) {
    return new HandlebarsRenderContext(options.getOutputFormat().getPath());
  }
}
