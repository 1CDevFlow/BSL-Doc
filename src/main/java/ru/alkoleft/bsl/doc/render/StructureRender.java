package ru.alkoleft.bsl.doc.render;

import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.PageType;
import ru.alkoleft.bsl.doc.render.contexts.ContextFactory;
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

public class StructureRender implements StructureVisitor {
  private final OutputProcessor outputProcessor;
  private final Stack<List<Path>> children = new Stack<>();
  private final ContentModel contentModel;
  private boolean withRoot = false;
  private int subsystemLevel = 0;
  private PathResolver pathResolver;

  public StructureRender(OutputProcessor outputProcessor, ContentModel contentModel) {
    this.outputProcessor = outputProcessor;
    this.contentModel = contentModel;
  }

  public void render(List<Item> structure, Path destination) {
    pathResolver = new PathResolver(destination, outputProcessor.getFormat());
    children.clear();
    subsystemLevel = 0;
    withRoot = structure.size() > 1;

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
    if (!withRoot && isRoot) {
      item.accentChildren(this);
      renderSubsystemPage(item);
    } else {
      pathResolver.entrance(item.getName());
      item.accentChildren(this);
      renderSubsystemPage(item);
      pathResolver.exit();
    }
    subsystemLevel--;
//    var childrenItems = children.pop().stream()
//        .map(it -> it.toString().substring(path.toString().length() + 1))
//        .collect(Collectors.toList());
//    if (!childrenItems.isEmpty()) {
//      var itemPath = path.resolve("index.md");
//      if (outputProcessor.needRender(itemPath)) {
//        var content = outputProcessor.getRender().render((MDSubsystem) item.getObject(), subsystemLevel, childrenItems);
//        outputProcessor.save(itemPath, content);
//      }
//      children.peek().add(itemPath);
//    }
  }

  @Override
  @SneakyThrows
  public void visit(ModuleItem item, int index) {
    var moduleContext = BslContext.getCurrent().buildFilteredModuleContext(item.getModule());
    if (moduleContext.isEmpty()) {
      return;
    }
    var path = pathResolver.getFilePath(item.getModule().getOwner().getName());
    if (!Files.exists(path.getParent())) {
      Files.createDirectories(path.getParent());
    }
    children.peek().add(path);
    if (outputProcessor.needRender(path)) {
      var context = ContextFactory.create(moduleContext, index);

      var content = BslRender.renderModule(moduleContext, index);
      outputProcessor.save(path, content);
      contentModel.append(path, PageType.MODULE);
    }
  }

  @Override
  public void visit(MDObjectItem item, int index) {
    item.accentChildren(this);
  }

  private void renderSubsystemPage(SubsystemItem item) {
    var path = pathResolver.getFilePath("index");

    if (outputProcessor.needRender(path)) {
      var context = ContextFactory.create(item.getSubsystem(), 0, subsystemLevel);
      context.setContentModel(contentModel);
      context.setOutputPath(path.getParent());
      var content = BslRender.renderSubsystem(context);
      outputProcessor.save(path, content);
      contentModel.append(path, PageType.SUBSYSTEM);
    }
  }
}
