package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class Shifter implements Helper<Object> {
  private int shift = 0;

  @Override
  public Object apply(Object context, Options options) throws IOException {
    var buffer = options.buffer();
    var startedShift = shift;

    int delta = context instanceof Integer ? (Integer) context : 0;
    shift += delta;
    shift++;
    var content = options.fn();
    shift = startedShift;
    if (startedShift + delta > 0) {
      var newChar = "\n" + new String(new char[2 * (startedShift + delta)]).replace('\0', ' ');
      buffer.append(content.toString().replace("\n", newChar));
    } else {
      buffer.append(content);
    }
    return buffer;
  }
}
