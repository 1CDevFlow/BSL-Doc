package ru.alkoleft.bsl.doc.render;

import lombok.Getter;

@Getter
public enum OutputFormat {
  Docusaurus("docusaurus"),
  Markdown("md"),
  ConfluenceMarkdown("confluence-md");

  private final String path;

  OutputFormat(String path) {
    this.path = path;
  }
}
