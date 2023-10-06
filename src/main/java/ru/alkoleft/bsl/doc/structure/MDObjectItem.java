package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;

public class MDObjectItem extends Item {
  public MDObjectItem(AbstractMDObjectBase object) {
    super(object, object.getName());
  }

  @Override
  public String getPresent() {
    var mdo = (AbstractMDObjectBase) getObject();
    return String.format("%s.%s", mdo.getMdoType().name(), mdo.getName());
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }
}
