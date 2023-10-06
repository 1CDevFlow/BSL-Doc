package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.render.RenderContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HandlebarsRenderContext implements RenderContext {

  private final String path;
  private final Handlebars handlebars;
  private final Map<String, Template> loadedTemplates = new HashMap<>();

  private final HandleLinks linksRender;

  public HandlebarsRenderContext(String path) {
    this.path = path;
    handlebars = new Handlebars().with(value -> value);
    handlebars.registerHelper("links", linksRender = new HandleLinks());
    handlebars.registerHelper("shift", new Shifter());
    handlebars.registerHelper("debug", new RenderDebugger());
    handlebars.registerHelper("great", new HelperGreat());
    handlebars.registerHelper("single-line", new SingleLineHelper());
  }

  public void setContext(BslContext context) {
    linksRender.context = context;
  }

  @Override
  public HandlebarItemRender getRender(String name) throws IOException {
    return new HandlebarItemRender(getTemplate(name));
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
}
