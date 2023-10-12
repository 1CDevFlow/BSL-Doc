package ru.alkoleft.bsl.doc.render.contexts;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class BaseContext {
  private final int index;
  private final String name;
  private final String present;
  private final String description;
  @Setter
  private Path outputPath;
}
