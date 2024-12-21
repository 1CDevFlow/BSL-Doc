package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
public class Links implements Helper<String> {
  private static final Pattern LINK_PATTERN = Pattern.compile("см\\. (([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)");
  private static final Pattern WARNING_PATTERN = Pattern.compile("(\\n\\s*\\n|^)(Важно[\\s\\S]+)(\\n\\s*\\n|$)");
  @Setter
  private BslContext context;

  @Override
  public Object apply(String context, Options options) {
    return context;
  }

  @SneakyThrows
  private String replaceTo(String baseValue, ru.alkoleft.bsl.doc.bsl.Links.Link link, Options options) {
    var methodInfo = BslContext.getCurrent().getMethodInfo(link);
    if (methodInfo == null || methodInfo.isPublishing() || methodInfo.getMethod() == null) {
      return String.format("[%s](%s)", baseValue, getLink(link));
    }

    var context = Context.newBuilder(methodInfo.getMethod().getReturnedValue()).build();
    return baseValue + options.apply(options.fn, context).toString();
  }

  private String handleWarning(String content) {
    // TODO Реализовать и другие блоки https://docusaurus.io/docs/markdown-features/admonitions
    if (content.contains("Важно")) {
      var matcher = WARNING_PATTERN.matcher(content);
      content = matcher
          .replaceAll(m -> m.group(1) + ":::tip важно\n\n" + matcher.group(2) + "\n\n:::" + matcher.group(3));
    }
    return content;
  }

  private String getLink(ru.alkoleft.bsl.doc.bsl.Links.Link link) {
    if (link.ownerName() != null && link.methodName() != null) {
      return link.ownerName() + "#" + link.methodName().toLowerCase(Locale.ROOT);
    } else if (link.ownerName() != null) {
      return link.ownerName();
    } else if (link.methodName() != null) {
      return "#" + link.methodName().toLowerCase(Locale.ROOT);
    } else {
      return "";
    }
  }
}
