package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.ModuleOwner;
import com.github._1c_syntax.bsl.mdo.Subsystem;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.helpers.BslFilter;

@UtilityClass
public class Factory {
  public Item createSubSystemItem(Subsystem subsystem, BslContext context) {
    var item = new SubsystemItem(subsystem);
    fillChildrenSubsystems(item, context);
    fillChildrenObjects(item, context);
    return item;
  }

  public Item createMDObjectItem(MD owner) {
    var item = new MDObjectItem(owner);
    if (owner instanceof ModuleOwner moduleOwner) {
      moduleOwner.getModules()
          .stream()
          .filter(BslFilter::checkModule)
          .map(ModuleItem::new)
          .forEach(item.getChildren()::add);
    }
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
