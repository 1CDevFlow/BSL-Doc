package ru.alkoleft.bsl.doc.bsl;

import lombok.Builder;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

@Value
@Builder
public class MethodInfo {
  MethodSymbol method;
  ModuleContext module;
  boolean publishing;
}
