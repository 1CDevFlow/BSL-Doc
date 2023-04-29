package ru.alkoleft.bsl.doc.render;

import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.io.IOException;

public interface RenderContext {
  ItemRender getRender(String name) throws IOException;

  void setContext(BslContext context);
}
