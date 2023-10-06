package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.List;

public class HelperGreat implements Helper<Object> {
  @Override
  public Object apply(Object context, Options options) throws IOException {
    int secondValue = options.param(0, 0);
    boolean success = false;
    if (context instanceof Integer) {
      success = ((Integer) context) > secondValue;
    }
    if (context instanceof List) {
      success = ((List) context).size() > secondValue;
    }

    if (success) {
      return options.fn(this);
    }
    return options.inverse(this);
  }
}
