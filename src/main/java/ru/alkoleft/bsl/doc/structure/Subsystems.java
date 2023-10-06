package ru.alkoleft.bsl.doc.structure;


import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.AllArgsConstructor;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Subsystems {

  final BslContext context;

  public List<Item> buildStructure() {

    return context.getRootSubsystems()
        .filter(it -> context.getFilter().getRootSubsystems().contains(it.getName()))
        .map(this::createSubSystemItem)
        .collect(Collectors.toList());
  }

  private Item createSubSystemItem(MDSubsystem subsystem) {
    var item = new SubsystemItem(subsystem);
    fillChildrenSubsystems(item);
    fillChildrenObjects(item);
    return item;
  }

  private Item createMDObjectItem(AbstractMDObjectBSL owner) {
    var item = new MDObjectItem(owner);
    owner.getModules()
        .stream()
        .filter(Utils::isManagerModule)
        .map(ModuleItem::new)
        .forEach(item.getChildren()::add);
    return item;
  }

  private void fillChildrenObjects(Item item) {
    context.getSubsystemObjects((MDSubsystem) item.getObject())
        .map(this::createMDObjectItem)
        .filter(it -> !it.getChildren().isEmpty())
        .forEach(item.getChildren()::add);
  }

  private void fillChildrenSubsystems(Item subsystemItem) {
    context.getChildrenSubsystems((MDSubsystem) subsystemItem.getObject())
        .forEach(it -> {
          var item = createSubSystemItem(it);
          subsystemItem.getChildren().add(item);
        });
  }
}
