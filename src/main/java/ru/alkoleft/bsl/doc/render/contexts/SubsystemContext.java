package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class SubsystemContext {
  int index;
  String name;
  String present;
  String description;
  List<String> children;
  int level;
}
