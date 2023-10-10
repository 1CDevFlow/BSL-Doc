package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Debugger implements Helper<Object> {
  @Override
  public Object apply(Object context, Options options) throws IOException {
    log.debug(options.fn().toString());
    return null;
  }
}