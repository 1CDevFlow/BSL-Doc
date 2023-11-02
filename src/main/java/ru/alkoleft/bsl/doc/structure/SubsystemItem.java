package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.bsl.mdo.Subsystem;

public class SubsystemItem extends Item {
  public SubsystemItem(Subsystem subsystem) {
    super(subsystem, subsystem.getName());
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }

  public Subsystem getSubsystem() {
    return (Subsystem) getObject();
  }
}
