package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;

public class SubsystemItem extends Item {
  public SubsystemItem(MDSubsystem subsystem) {
    super(subsystem, subsystem.getName());
  }

  @Override
  public void accept(StructureVisitor visitor, int index) {
    visitor.visit(this, index);
  }

  public MDSubsystem getSubsystem() {
    return (MDSubsystem) getObject();
  }
}
