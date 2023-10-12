package ru.alkoleft.bsl.doc.model;

import lombok.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class ContentModel {
  List<Page> pages = new ArrayList<>();

  public void append(Path path, PageType type) {
    pages.add(new Page(path, null, type));
  }

  public List<Page> getChildrenPages(Path path) {
    return pages.stream()
        .filter(it -> isChild(it, path))
        .collect(Collectors.toList());
  }

  boolean isChild(Page page, Path path) {
    var pagePath = page.getPath();
    if (pagePath.getFileName().toString().startsWith("index.")) {
      return pagePath.getParent().getParent().equals(path);
    } else {
      return pagePath.getParent().equals(path);
    }
  }
}
