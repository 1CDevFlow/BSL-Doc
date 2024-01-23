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
  public ModuleContext(int index,
                       String name,
                       String present,
                       String description,
                       Path outputPath,
                       boolean isCommonModule,
                       String ownerType,
                       String moduleType,
                       List<MethodSymbol> methods) {
    super(index, name, present, description, outputPath);
    this.isCommonModule = isCommonModule;
    this.ownerType = ownerType;
    this.moduleType = moduleType;
    this.methods = methods;
  }
}
