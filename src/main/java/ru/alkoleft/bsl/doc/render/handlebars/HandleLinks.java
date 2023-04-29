package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Pattern;

public class HandleLinks implements Helper<String> {
  Pattern pattern = Pattern.compile("см\\. (([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)");

  BslContext context;

  @Override
  public Object apply(String context, Options options) throws IOException {
    var matcher = pattern.matcher(context);
    if (matcher.find()) {
      return matcher.replaceAll(matchResult -> String.format("[%s](%s)", matchResult.group(0), getLink(matcher.group(2), matcher.group(3))));
    }
    return context;
  }

  private String getLink(String owner, String method) {
    if (owner != null && method != null) {
      return owner + "#" + method.toLowerCase(Locale.ROOT);
    } else if (context.contains(method)) {
      return method;
    } else if (method != null) {
      return "#" + method.toLowerCase(Locale.ROOT);
    } else{
      return "";
    }
  }
}
