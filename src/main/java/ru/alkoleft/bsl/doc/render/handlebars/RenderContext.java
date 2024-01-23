package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.URLTemplateLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.helpers.Strings;
import ru.alkoleft.bsl.doc.render.TemplatesDefinition;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Debugger;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Links;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.MdoPresent;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.PageLink;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.Shifter;
import ru.alkoleft.bsl.doc.render.handlebars.helpers.SingleLine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RenderContext {
  private final URL baseURL;
  private final Handlebars handlebars;
  private final Map<String, Template> loadedTemplates = new HashMap<>();
  private final Links linksRender;
  private final TemplatesDefinition templatesDefinition;

  private RenderContext(TemplatesDefinition templatesDefinition) {
    this.templatesDefinition = templatesDefinition;
    this.baseURL = getClass().getClassLoader().getResource(templatesDefinition.path());
    handlebars = new Handlebars()
        .with(new URLTemplateLoader() {
          @Override
          protected URL getResource(String location) throws IOException {
            return new URL(baseURL + location);
          }
        })
        .with(new ConcurrentMapTemplateCache());

    handlebars.registerHelper("great", ConditionalHelpers.gt);
    handlebars.registerHelper("eq", ConditionalHelpers.eq);

    handlebars.registerHelper("links", linksRender = new Links());
    handlebars.registerHelper("mdo-present", new MdoPresent());
    handlebars.registerHelper("shift", new Shifter());
    handlebars.registerHelper("debug", new Debugger());
    handlebars.registerHelper("single-line", new SingleLine());
    handlebars.registerHelper("page-link", new PageLink());
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

  @SneakyThrows
  private Template getTemplate(String name) {
    if (loadedTemplates.containsKey(name)) {
      return loadedTemplates.get(name);
    }
    Template template;
    if (Strings.isNullOrEmpty(templatesDefinition.headerTemplate())
      && Strings.isNullOrEmpty(templatesDefinition.footerTemplate())) {
      template = handlebars.compile(name);
    } else {
      var builder = new StringBuilder();
      if (!Strings.isNullOrEmpty(templatesDefinition.headerTemplate())) {
        builder.append(templatesDefinition.headerTemplate()).append('\n');
      }
      builder.append(handlebars.getLoader().sourceAt(name).content(handlebars.getCharset()));
      if (!Strings.isNullOrEmpty(templatesDefinition.footerTemplate())) {
        builder.append('\n').append(templatesDefinition.footerTemplate());
      }
      template = handlebars.compileInline(builder.toString());
    }
    loadedTemplates.put(name, template);
    return template;
  }

  @UtilityClass
  public static class Factory {
    public RenderContext create(TemplatesDefinition templatesDefinition) {
      return new RenderContext(templatesDefinition);
    }
  }
}
