package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.render.processor.OutputProcessor;
import ru.alkoleft.bsl.doc.structure.Item;
import ru.alkoleft.bsl.doc.structure.MDObjectItem;
import ru.alkoleft.bsl.doc.structure.ModuleItem;
import ru.alkoleft.bsl.doc.structure.StructureVisitor;
import ru.alkoleft.bsl.doc.structure.SubsystemItem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class StructureRender implements StructureVisitor {
  private final OutputProcessor outputProcessor;
  private final Stack<Path> paths = new Stack<>();
  private final Stack<List<Path>> children = new Stack<>();
  private boolean withRoot = false;
  private int subsystemLevel = 0;

  public StructureRender(OutputProcessor outputProcessor) {
    this.outputProcessor = outputProcessor;
  }

  public void render(List<Item> structure, Path destination) {
    paths.clear();
    children.clear();
    subsystemLevel = 0;
    withRoot = structure.size() > 1;

    paths.push(destination);
    children.push(new ArrayList<>());
    for (int i = 0; i < structure.size(); i++) {
      structure.get(i).accept(this, i);
    }
  }

  @Override
  public void visit(SubsystemItem item, int index) {
    var isRoot = subsystemLevel == 0;
    subsystemLevel++;
    children.push(new ArrayList<>());
    Path path;
    if (!withRoot && isRoot) {
      path = paths.peek();
      item.accentChildren(this);
    } else {
      path = paths.peek().resolve(item.getName());
      paths.push(path);
      item.accentChildren(this);
      paths.pop();
    }
    subsystemLevel--;
    var childrenItems = children.pop().stream()
        .map(it -> it.toString().substring(path.toString().length() + 1))
        .collect(Collectors.toList());
    if (!childrenItems.isEmpty()) {
      var itemPath = path.resolve("index.md");
      if (outputProcessor.needRender(itemPath)) {
        var content = outputProcessor.getRender().render((MDSubsystem) item.getObject(), subsystemLevel, childrenItems);
        outputProcessor.save(itemPath, content);
      }
      children.peek().add(itemPath);
    }
  }

  @Override
  @SneakyThrows
  public void visit(ModuleItem item, int index) {
    var moduleContext = BslContext.getCurrent().buildFilteredModuleContext(item.getModule());
    if (moduleContext.isEmpty()) {
      return;
    }
    var path = paths.peek().resolve(item.getModule().getOwner().getName() + ".md");
    if (!Files.exists(path.getParent())) {
      Files.createDirectories(path.getParent());
    }
    children.peek().add(path);
    if (outputProcessor.needRender(path)) {
      var content = outputProcessor.getRender().render(moduleContext, index);
      outputProcessor.save(path, content);
    }
  }

  @Override
  public void visit(MDObjectItem item, int index) {
    item.accentChildren(this);
  }
}
