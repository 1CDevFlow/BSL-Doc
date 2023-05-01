package ru.alkoleft.bsl.doc.render.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.Links;

import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
public class HandleLinks implements Helper<String> {
  Pattern pattern = Pattern.compile("см\\. (([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)");

  Pattern warningPattern = Pattern.compile("(\\n\\s*\\n|^)(Важно[\\s\\S]+)(\\n\\s*\\n|$)");
  BslContext context;

  @Override
  public Object apply(String context, Options options) {
    var matcher = pattern.matcher(context);
    String result;
    if (matcher.find()) {
      result = matcher.replaceAll(matchResult -> replaceTo(matcher.group(0), Links.createLink(matcher.group(2), matcher.group(3)), options));
    } else {
      result = context;
    }

    result = handleWarning(result);

    return result;
  }

  @SneakyThrows
  private String replaceTo(String baseValue, Links.Link link, Options options) {
    var methodInfo = BslContext.getCurrent().getMethodInfo(link);
    if (methodInfo == null || methodInfo.isPublishing()) {
      return String.format("[%s](%s)", baseValue, getLink(link));
    }

    var context = Context.newBuilder(methodInfo.getMethod().getReturnedValue()).build();
    return baseValue + options.apply(options.fn, context).toString();
  }

  private String handleWarning(String content) {
    // TODO Реализовать и другие блоки https://docusaurus.io/docs/markdown-features/admonitions
    if (content.contains("Важно")) {
      var matcher = warningPattern.matcher(content);
      content = matcher.replaceAll(m -> m.group(1) + ":::tip важно\n\n" + matcher.group(2) + "\n\n:::" + matcher.group(3));
    }
    return content;
  }

  private String getLink(Links.Link link) {
    if (link.getOwnerName() != null && link.getMethodName() != null) {
      return link.getOwnerName() + "#" + link.getMethodName().toLowerCase(Locale.ROOT);
    } else if (link.getOwnerName() != null) {
      return link.getOwnerName();
    } else if (link.getMethodName() != null) {
      return "#" + link.getMethodName().toLowerCase(Locale.ROOT);
    } else {
      return "";
    }
  }
}
