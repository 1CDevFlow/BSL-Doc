package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.List;

@Value
@Builder
public class ModuleContext {
  int index;
  String name;
  String present;
  String description;
  List<MethodSymbol> methods;
}
