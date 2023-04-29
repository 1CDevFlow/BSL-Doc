package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Template;
import ru.alkoleft.bsl.doc.render.ItemRender;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class HandlebarItemRender implements ItemRender {

  private final Template template;

  HandlebarItemRender(Template template) {
    this.template = template;
  }

  Map<String, Object> context = new HashMap<>();

  @Override
  public void put(String key, Object value) {
    context.put(key, value);
  }

  @Override
  public void renderToFile(Path fileName) throws IOException {
    try (FileWriter writer = new FileWriter(fileName.toFile())) {
      template.apply(context, writer);
    }
  }
}
