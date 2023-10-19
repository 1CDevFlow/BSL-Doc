package ru.alkoleft.bsl.doc.render;

import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.helpers.MDOHelper;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.Links;
import ru.alkoleft.bsl.doc.model.PageType;
import ru.alkoleft.bsl.doc.render.contexts.ContextFactory;
import ru.alkoleft.bsl.doc.render.output.OutputStrategy;
import ru.alkoleft.bsl.doc.structure.Item;
import ru.alkoleft.bsl.doc.structure.MDObjectItem;
import ru.alkoleft.bsl.doc.structure.ModuleItem;
import ru.alkoleft.bsl.doc.structure.StructureVisitor;
import ru.alkoleft.bsl.doc.structure.SubsystemItem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class StructureRender implements StructureVisitor {
  private final OutputStrategy outputStrategy;
  private final ContentModel contentModel;
  private boolean withRoot = false;
  private int subsystemLevel = 0;
  private PathResolver pathResolver;

  public StructureRender(OutputStrategy outputStrategy, ContentModel contentModel) {
    this.outputStrategy = outputStrategy;

    this.contentModel = Objects.requireNonNullElseGet(contentModel, ContentModel::new);
  }

  public void render(List<Item> structure, Path destination) {
    pathResolver = new PathResolver(destination, outputStrategy.getFormat());
    subsystemLevel = 0;
    withRoot = structure.size() > 1;

    for (int i = 0; i < structure.size(); i++) {
      structure.get(i).accept(this, i);
    }
  }

  @Override
  public void visit(SubsystemItem item, int index) {
    var isRoot = subsystemLevel == 0;
    subsystemLevel++;
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
  }

  @Override
  @SneakyThrows
  public void visit(ModuleItem item, int index) {
    var moduleContext = BslContext.getCurrent().getModuleContext(item.getModule());
    if (moduleContext.isEmpty()) {
      return;
    }
    var path = pathResolver.getFilePath(MDOHelper.getOwner(item.getModule()).getName());
    if (!Files.exists(path.getParent())) {
      Files.createDirectories(path.getParent());
    }
    if (outputStrategy.needRender(path)) {
      Links.setCurrentPath(path);
      var content = BslRender.renderModule(moduleContext, index);
      outputStrategy.save(path, content);
      contentModel.append(path, PageType.MODULE);
    }
  }

  @Override
  public void visit(MDObjectItem item, int index) {
    item.accentChildren(this);
  }

  private void renderSubsystemPage(SubsystemItem item) {
    var path = pathResolver.getFilePath("index");

    if (outputStrategy.needRender(path)) {
      var context = ContextFactory.create(item.getSubsystem(), 0, subsystemLevel);
      context.setContentModel(contentModel);
      context.setOutputPath(path.getParent());
      Links.setCurrentPath(path);
      var content = BslRender.renderSubsystem(context);
      outputStrategy.save(path, content);
      contentModel.append(path, PageType.SUBSYSTEM);
    }
  }
}
