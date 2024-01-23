package ru.alkoleft.bsl.doc.bsl;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.helpers.Strings;

import java.util.regex.Pattern;

@UtilityClass
public class Links {
  private static final Pattern PATTERN_WITH_SEE =
    Pattern.compile("^см\\. (([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)$");
  private static final Pattern PATTERN = Pattern.compile("^(([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)$");

  public Link parseLink(String link, boolean withSee) {
    var matcher = (withSee ? PATTERN_WITH_SEE : PATTERN).matcher(link);
    if (matcher.find()) {
      return createLink(matcher.group(2), matcher.group(3));
    }
    return null;
  }

  public Link createLink(String owner, String method) {
    if (owner != null && method != null) {
      return new Link(owner, method);
    } else if (BslContext.getCurrent().contains(method)) {
      return new Link(method, null);
    } else if (method != null) {
      return new Link(null, method);
    } else {
      return null;
    }
  }

  public record Link(String ownerName, String methodName) {
    public String getFullName() {
      if (Strings.isNullOrEmpty(ownerName)) {
        return methodName;
      } else if (Strings.isNullOrEmpty(methodName)) {
        return ownerName;
      } else {
        return ownerName + "." + methodName;
      }
    }
  }
}
