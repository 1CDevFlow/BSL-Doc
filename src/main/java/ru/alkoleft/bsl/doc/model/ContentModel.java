package ru.alkoleft.bsl.doc.model;

import lombok.Value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Value
public class ContentModel {
  List<Page> pages = new ArrayList<>();

  public void append(Path path, PageType type) {
    var page = pages.stream()
        .filter(it -> path.equals(it.getPath()))
        .findAny();

    if (page.isEmpty()) {
      pages.add(new Page(path, null, type));
    } else {
      page.get().setType(type);
    }
  }

  public List<Page> getChildrenPages(Path path) {
    return pages.stream()
        .filter(it -> isChild(it, path))
        .sorted(Comparator.comparing(Page::getPath))
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
