package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;

@UtilityClass
public class Factory {
  public Item createSubSystemItem(MDSubsystem subsystem, BslContext context) {
    var item = new SubsystemItem(subsystem);
    fillChildrenSubsystems(item, context);
    fillChildrenObjects(item, context);
    return item;
  }

  public Item createMDObjectItem(AbstractMDObjectBSL owner) {
    var item = new MDObjectItem(owner);
    owner.getModules()
        .stream()
        .filter(Utils::isManagerModule)
        .map(ModuleItem::new)
        .forEach(item.getChildren()::add);
    return item;
  }

  private void fillChildrenObjects(SubsystemItem item, BslContext context) {
    context.getSubsystemObjects(item.getSubsystem())
        .map(Factory::createMDObjectItem)
        .filter(it -> !it.getChildren().isEmpty())
        .forEach(item.getChildren()::add);
  }

  private void fillChildrenSubsystems(SubsystemItem item, BslContext context) {
    context.getChildrenSubsystems(item.getSubsystem())
        .map(it -> createSubSystemItem(it, context))
        .forEach(item.getChildren()::add);
  }

}
