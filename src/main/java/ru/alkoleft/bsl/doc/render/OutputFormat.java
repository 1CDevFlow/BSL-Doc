package ru.alkoleft.bsl.doc.render;

import lombok.Getter;

public enum OutputFormat {
  Docusaurus("docusaurus"),
  Markdown("md");

  @Getter
  private final String path;

  OutputFormat(String path) {
    this.path = path;
  }
}
