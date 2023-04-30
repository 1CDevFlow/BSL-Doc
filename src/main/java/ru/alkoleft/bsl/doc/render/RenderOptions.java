package ru.alkoleft.bsl.doc.render;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RenderOptions {
  OutputFormat outputFormat;

  boolean subsystemHierarchy;
}
