package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.bsl.mdo.MD;

public class MDObjectItem extends Item {
  public MDObjectItem(MD object) {
    super(object, object.getName());
  }

  @Override
  public String getPresent() {
    var mdo = getMDObject();
    return String.format("%s.%s", mdo.getMdoType().name(), mdo.getName());
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }

  public MD getMDObject() {
    return (MD) getObject();
  }
}
