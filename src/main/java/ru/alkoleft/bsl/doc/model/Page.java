package ru.alkoleft.bsl.doc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class Page {
  private Path path;
  private String title;
  private PageType type;
}
