package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;

public class ModuleItem extends Item {
  public ModuleItem(MDOModule module) {
    super(module, module.getModuleType().name());
  }

  public MDOModule getModule() {
    return (MDOModule) getObject();
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }
}
