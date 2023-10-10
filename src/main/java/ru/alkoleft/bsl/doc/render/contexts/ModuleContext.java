package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.Getter;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.List;

@Builder
@Getter
public class ModuleContext {
  int index;
  String name;
  String present;
  String description;
  boolean isCommonModule;
  String ownerType;
  String moduleType;
  List<MethodSymbol>methods;

}
