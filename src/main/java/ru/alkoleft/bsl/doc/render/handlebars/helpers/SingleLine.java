package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;

import java.io.IOException;

public class SingleLine implements Helper<Object> {
  @Override
  public Object apply(Object context, Options options) throws IOException {
    var buffer = options.buffer();

    String content;
    if (options.tagType == TagType.SECTION) {
      content = options.fn().toString();
    } else {
      content = context.toString();
    }

    buffer.append(content.replace("\n", "<br/>"));
    return buffer;
  }
}
