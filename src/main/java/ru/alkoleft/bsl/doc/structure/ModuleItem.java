package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.bsl.mdo.Module;

public class ModuleItem extends Item {
  public ModuleItem(Module module) {
    super(module, module.getModuleType().name());
  }

  public Module getModule() {
    return (Module) getObject();
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }
}
