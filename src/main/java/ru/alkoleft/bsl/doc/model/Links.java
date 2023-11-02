package ru.alkoleft.bsl.doc.model;

import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class Links {
  @Setter
  private Path currentPath;

  public String getPageLink(Page from, Page to) {
    return from.getPath().relativize(to.getPath()).toString();
  }

  public String getPageLink(Page to) {
    return currentPath.getParent().relativize(to.getPath()).toString();
  }
}
