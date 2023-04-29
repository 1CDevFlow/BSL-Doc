package ru.alkoleft.bsl.doc.render;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.render.handlebars.HandlebarsRenderContext;
import ru.alkoleft.bsl.doc.render.velocity.VelocityRenderContext;

@UtilityClass
public class Factory {
  public Render createRender(RenderOptions options) {
    return new Render(null);
  }

  public RenderContext createRenderContext(RenderOptions options) {
    return new HandlebarsRenderContext(options.getOutputFormat().getPath());
  }

  public RenderContext createVelocityRenderContext(RenderOptions options) {
    return new VelocityRenderContext(options.getOutputFormat().getPath());
  }
}
