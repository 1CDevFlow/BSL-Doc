package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
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
  Render render;
  int subsystemLevel = 0;

  Stack<Path> paths = new Stack<>();
  Stack<List<Path>> children = new Stack<>();

  boolean withRoot = false;

  public void render(Render render, List<Item> structure, Path destination) {
    this.render = render;
    paths.push(destination);
    children.push(new ArrayList<>());
    for (int i = 0; i < structure.size(); i++) {
      structure.get(i).accept(this, i);
    }
  }

  @Override
  public void visit(SubsystemItem item, int index) {
    boolean isRoot = subsystemLevel == 0;
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
    List<String> childrenItems =
        children.pop().stream()
            .map(it -> it.toString().substring(path.toString().length() + 1))
            .collect(Collectors.toList());
    if (!childrenItems.isEmpty()) {
      render.render((MDSubsystem) item.getObject(), path.resolve("index.md"), subsystemLevel, childrenItems);
      children.peek().add(path.resolve("index.md"));
    }
  }

  @Override
  @SneakyThrows
  public void visit(ModuleItem item, int index) {
    var moduleContext = BslContext.getCurrent().buildFilteredModuleContext(item.getModule());
    if (moduleContext.isEmpty()) {
      return;
    }
    final var path = paths.peek().resolve(item.getModule().getOwner().getName() + ".md");
    if (!Files.exists(path.getParent())) {
      Files.createDirectories(path.getParent());
    }
    children.peek().add(path);
    render.render(moduleContext, path, index);
  }

  @Override
  public void visit(MDObjectItem item, int index) {
    item.accentChildren(this);
  }
}
