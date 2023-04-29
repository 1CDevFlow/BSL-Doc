package ru.alkoleft.bsl.doc.render.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.render.ItemRender;
import ru.alkoleft.bsl.doc.render.RenderContext;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

public class VelocityRenderContext implements RenderContext {
  VelocityEngine velocityEngine;
  String path;

  public VelocityRenderContext(String name) {
    path = name;

    velocityEngine = new VelocityEngine();
    Properties properties = new Properties();
    properties.setProperty("resource.loaders", "file");
//        properties.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.AvalonLogChute");
    properties.setProperty("resource.loader.file.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.init(properties);
    velocityEngine.init();
  }

  public Template getTemplate(String name) {
    return velocityEngine.getTemplate(String.format("%s/%s.vm", path, name));
  }

  @Override
  public ItemRender getRender(String name) throws IOException {
    return new VelocityItemRender(getTemplate(name));
  }

  @Override
  public void setContext(BslContext context) {

  }
}
