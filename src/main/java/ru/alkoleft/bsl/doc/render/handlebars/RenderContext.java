package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.render.RenderOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RenderContext {

  private final String path;
  private final Handlebars handlebars;
  private final Map<String, Template> loadedTemplates = new HashMap<>();

  private final HandleLinks linksRender;

  private RenderContext(String path) {
    this.path = path;
    handlebars = new Handlebars().with(value -> value);
    handlebars.registerHelper("links", linksRender = new HandleLinks());
    handlebars.registerHelper("shift", new Shifter());
    handlebars.registerHelper("debug", new RenderDebugger());
    handlebars.registerHelper("great", ConditionalHelpers.gt);
    handlebars.registerHelper("eq", ConditionalHelpers.eq);
    handlebars.registerHelper("single-line", new SingleLineHelper());
  }

  public void setContext(BslContext context) {
    linksRender.context = context;
  }

  @SneakyThrows
  public void renderToFile(String templateName, Context context, Path output){
    var template = getTemplate(templateName);
    try (FileWriter writer = new FileWriter(output.toFile())) {
      template.apply(context, writer);
    }
  }

  private Template getTemplate(String name) throws IOException {
    if (loadedTemplates.containsKey(name)) {
      return loadedTemplates.get(name);
    }
    var location = String.format("%s/%s", path, name);
    var template = handlebars.compile(location);
    loadedTemplates.put(name, template);
    return template;
  }

  @UtilityClass
  public static class Factory{
    public RenderContext create(RenderOptions options) {
      return new RenderContext(options.getOutputFormat().getPath());
    }
  }
}
