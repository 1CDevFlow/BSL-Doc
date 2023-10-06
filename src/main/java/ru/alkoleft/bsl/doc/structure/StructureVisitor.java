package ru.alkoleft.bsl.doc.structure;

import java.util.List;

public interface StructureVisitor {
  void visit(SubsystemItem item, int index);

  void visit(ModuleItem item, int index);

  void visit(MDObjectItem item, int index);
}
