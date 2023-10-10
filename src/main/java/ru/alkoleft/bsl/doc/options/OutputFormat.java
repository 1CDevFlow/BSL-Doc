package ru.alkoleft.bsl.doc.options;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutputFormat {
  Docusaurus("docusaurus", "md"),
  Markdown("md", "md"),
  ConfluenceMarkdown("confluence-md", "md");

  private final String path;
  private final String extension;

}
