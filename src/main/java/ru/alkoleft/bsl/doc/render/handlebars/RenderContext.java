package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.options.OutputOptions;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Debugger;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Links;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.MdoPresent;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Shifter;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.SingleLine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RenderContext {
  private final String path;
  private final Handlebars handlebars;
  private final Map<String, Template> loadedTemplates = new HashMap<>();
  private final Links linksRender;

  private RenderContext(String path) {
    this.path = path;
    handlebars = new Handlebars().with(value -> value);
    handlebars.registerHelper("links", linksRender = new Links());
    handlebars.registerHelper("mdo-present", new MdoPresent());
    handlebars.registerHelper("shift", new Shifter());
    handlebars.registerHelper("debug", new Debugger());
    handlebars.registerHelper("single-line", new SingleLine());
    handlebars.registerHelper("great", ConditionalHelpers.gt);
    handlebars.registerHelper("eq", ConditionalHelpers.eq);
  }

  public void setContext(BslContext context) {
    linksRender.setContext(context);
  }

  @SneakyThrows
  public void renderToFile(String templateName, Context context, Path output) {
    var template = getTemplate(templateName);
    try (var writer = new FileWriter(output.toFile())) {
      template.apply(context, writer);
    }
  }

  @SneakyThrows
  public String render(String templateName, Context context) {
    var template = getTemplate(templateName);
    var writer = new StringWriter();
    template.apply(context, writer);
    return writer.toString();
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
  public static class Factory {
    public RenderContext create(OutputOptions options) {
      return new RenderContext(options.getOutputFormat().getPath());
    }
  }
}
