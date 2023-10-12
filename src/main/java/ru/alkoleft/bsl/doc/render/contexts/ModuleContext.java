package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.nio.file.Path;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class ModuleContext extends BaseContext {
  boolean isCommonModule;
  String ownerType;
  String moduleType;
  List<MethodSymbol> methods;

  @Builder
  public ModuleContext(Path outputPath, int index, String name, String present, String description, boolean isCommonModule, String ownerType, String moduleType, List<MethodSymbol> methods) {
    super(outputPath, index, name, present, description);
    this.isCommonModule = isCommonModule;
    this.ownerType = ownerType;
    this.moduleType = moduleType;
    this.methods = methods;
  }
}
