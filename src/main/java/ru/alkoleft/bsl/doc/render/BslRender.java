package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;
import ru.alkoleft.bsl.doc.render.contexts.ContextFactory;
import ru.alkoleft.bsl.doc.render.contexts.ModuleContext;
import ru.alkoleft.bsl.doc.render.contexts.SubsystemContext;

@Slf4j
@UtilityClass
public class BslRender {

  @SneakyThrows
  public String renderModule(ModuleInfo module, int index) {
    return renderModule(ContextFactory.create(module, index));
  }

  @SneakyThrows
  public String renderModule(ModuleContext module) {
    log.debug("Render module '{}'", module.getName());
    return BaseRender.render("module", ContextFactory.createContext(module));
  }

  @SneakyThrows
  public String renderSubsystem(MDSubsystem subsystem, int level) {
    return renderSubsystem(ContextFactory.create(subsystem, 0, level));
  }

  @SneakyThrows
  public String renderSubsystem(SubsystemContext subsystem) {
    log.debug("Render subsystem '{}'", subsystem.getName());
    return BaseRender.render("subsystem", ContextFactory.createContext(subsystem));
  }
}
