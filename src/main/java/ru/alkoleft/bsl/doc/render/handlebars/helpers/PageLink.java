package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import ru.alkoleft.bsl.doc.model.Links;
import ru.alkoleft.bsl.doc.model.Page;

import java.io.IOException;

public class PageLink implements Helper<Page> {
  @Override
  public Object apply(Page context, Options options) throws IOException {
    return Links.getPageLink(context);
  }
}
