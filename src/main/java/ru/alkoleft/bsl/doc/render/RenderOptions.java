package ru.alkoleft.bsl.doc.render;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class RenderOptions {
  OutputFormat outputFormat;

  boolean subsystemHierarchy;
  @Singular
  List<String> rootSubsystems;
}
