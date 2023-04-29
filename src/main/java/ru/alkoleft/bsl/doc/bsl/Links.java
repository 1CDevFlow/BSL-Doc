package ru.alkoleft.bsl.doc.bsl;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class Links {
  private final Pattern patternWithSee = Pattern.compile("^см\\. (([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)$");
  private final Pattern pattern = Pattern.compile("^(([\\wА-Яа-я\\.\\d]+)\\.)*([\\wА-Яа-я\\d]+)$");

  public Link parseLink(String link, boolean withSee) {
    var matcher = (withSee ? patternWithSee : pattern).matcher(link);
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

  @Value
  @AllArgsConstructor
  public static class Link {
    String ownerName;
    String methodName;

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
