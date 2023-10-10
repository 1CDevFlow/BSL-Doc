package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.List;

@Builder
@Value
public class ModuleContext {
  int index;
  String name;
  String present;
  String description;
  boolean isCommonModule;
  String ownerType;
  String moduleType;
  List<MethodSymbol> methods;
}
