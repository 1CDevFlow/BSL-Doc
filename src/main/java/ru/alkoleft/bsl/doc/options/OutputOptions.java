package ru.alkoleft.bsl.doc.options;

import lombok.Builder;
import lombok.Value;

import java.nio.file.Path;

@Builder
@Value
public class OutputOptions {
  OutputFormat outputFormat;
  boolean subsystemHierarchy;
  Path destination;
  ChildLayout childLayout;
}
