package ru.alkoleft.bsl.doc.structure;

import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.List;
import java.util.stream.Collectors;

public class SubsystemsStructureBuilder implements StructureBuilder {

  public List<Item> build(BslContext context) {

    return context.getRootSubsystems()
        .filter(it -> context.getFilter().getRootSubsystems().contains(it.getName()))
        .map(it -> Factory.createSubSystemItem(it, context))
        .collect(Collectors.toList());
  }
}
