package ru.alkoleft.bsl.doc.bsl;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Filter {
  boolean isExport;
  @Singular
  List<String> regions;
  @Singular
  List<String> rootSubsystems;
  @Singular
  List<String> modules;

}
