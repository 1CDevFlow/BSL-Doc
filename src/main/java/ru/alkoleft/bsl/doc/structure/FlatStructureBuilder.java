package ru.alkoleft.bsl.doc.structure;

import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.List;
import java.util.stream.Collectors;

public class FlatStructureBuilder implements StructureBuilder {

  @Override
  public List<Item> build(BslContext context) {
    return context.getModules()
        .map(ModuleItem::new)
        .collect(Collectors.toList());
  }
}
