package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class SubsystemTreeRender {

  StructureStrategy.Subsystems strategy;
  Render render;

  BslContext context;
  boolean withRoot;

  public SubsystemTreeRender(BslContext context, RenderContext renderContext, Path destination) {
    withRoot = context.getFilter().getRootSubsystems().size() > 1;
    render = new Render(renderContext);
    strategy = new StructureStrategy.Subsystems(destination, "md", withRoot);
    this.context = context;
  }

  public void render() {
    var rootSubsystems = context.getRootSubsystems()
        .filter(it -> context.getFilter().getRootSubsystems().contains(it.getName()));

    if (withRoot) {
      rootSubsystems.forEach(it -> renderSubsystem(it, 1));
    } else {
      rootSubsystems
          .forEach(it -> renderSubsystem(it, 0));
    }
  }

  private void renderSubsystem(MDSubsystem subsystem, int level) {
    render.render(subsystem, strategy, level);
    context.getChildrenSubsystems(subsystem)
        .forEach(it -> renderSubsystem(it, level + 1));
    AtomicInteger index = new AtomicInteger();
    context.getSubsystemObjects(subsystem)
        .flatMap(it->it.getModules().stream())
        .peek(it -> index.getAndIncrement())
        .map(context::buildModuleContext)
        .forEach(it -> render.render(it, strategy, index.get()));
  }
}
