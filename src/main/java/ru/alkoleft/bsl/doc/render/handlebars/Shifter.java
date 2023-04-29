package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;

import java.io.IOException;

public class Shifter implements Helper<Object> {
  private int shift = 0;

  @Override
  public Object apply(Object context, Options options) throws IOException {
    Options.Buffer buffer = options.buffer();
    int startedShift = shift;
    shift++;
    var content = options.fn();
    shift--;
    if (startedShift > 0) {
      var newChar = "\n" + new String(new char[startedShift]).replace('\0', '\t');
      buffer.append(content.toString().replace("\n", newChar));
    } else {
      buffer.append(content);
    }
    return buffer;
  }
}
