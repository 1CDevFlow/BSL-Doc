package ru.alkoleft.bsl.doc.render.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import ru.alkoleft.bsl.doc.render.ItemRender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.file.Path;

public class VelocityItemRender implements ItemRender {
  private final Template template;

  VelocityItemRender(Template template) {
    this.template = template;
  }

  VelocityContext context = new VelocityContext();

  public void put(String key, Object value) {
    context.put(key, value);
  }

  public void renderToFile(Path fileName) throws IOException {
    try (FileWriter writer = new FileWriter(fileName.toFile())) {
      template.merge(context, writer);
    }
  }

  public String renderToString() {
    StringWriter writer = new StringWriter();
    template.merge(context, writer);
    return writer.toString();
  }

  public void renderToConsole() {
    template.merge(context, new BufferedWriter(new OutputStreamWriter(System.out)));
  }
}
