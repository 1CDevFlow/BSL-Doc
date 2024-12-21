package ru.alkoleft.bsl.doc.render;

import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.bsl.helpers.MDOHelper;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.Links;
import ru.alkoleft.bsl.doc.model.PageType;
import ru.alkoleft.bsl.doc.options.ChildLayout;
import ru.alkoleft.bsl.doc.options.OutputOptions;
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

public class StructureRender implements StructureVisitor {
  private final OutputStrategy outputStrategy;
  private final ContentModel contentModel;
  private final OutputOptions outputOptions;
  private boolean withRoot = false;
  private int subsystemLevel = 0;
  private PathResolver pathResolver;

  public StructureRender(OutputOptions outputOptions, OutputStrategy outputStrategy, ContentModel contentModel) {
    this.outputStrategy = outputStrategy;
    this.contentModel = contentModel;
    this.outputOptions = outputOptions;
  }

  public void render(List<Item> structure, Path destination) {
    pathResolver = new PathResolver(destination, outputStrategy.getFormat());
    subsystemLevel = 0;
    withRoot = outputOptions.getChildLayout() == ChildLayout.SUB_DIRECTORY || structure.size() > 1;

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
      if (outputOptions.getChildLayout() == ChildLayout.SAME_DIRECTORY) {
        renderSubsystemPage(item);
        pathResolver.exit();
      } else {
        pathResolver.exit();
        renderSubsystemPage(item);
      }
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
      outputStrategy.save(path, content)
          .setType(PageType.MODULE);
    }
  }

  @Override
  public void visit(MDObjectItem item, int index) {
    item.accentChildren(this);
  }

  private void renderSubsystemPage(SubsystemItem item) {
    var path = getSubsystemPagePath(item);

    if (outputStrategy.needRender(path)) {
      var context = ContextFactory.create(item.getSubsystem(), 0, subsystemLevel);
      context.setContentModel(contentModel);
      if (outputOptions.getChildLayout() == ChildLayout.SAME_DIRECTORY) {
        context.setOutputPath(path.getParent());
      } else {
        context.setOutputPath(path.getParent().resolve(item.getName()));
      }
      Links.setCurrentPath(path);
      var content = BslRender.renderSubsystem(context);
      outputStrategy.save(path, content)
          .setType(PageType.SUBSYSTEM);
    }
  }

  private Path getSubsystemPagePath(SubsystemItem item) {
    return outputOptions.getChildLayout() == ChildLayout.SAME_DIRECTORY
        ? pathResolver.getFilePath("index")
        : pathResolver.getFilePath(item.getName());
  }
}
