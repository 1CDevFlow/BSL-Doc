package ru.alkoleft.bsl.doc.bsl;

import com.github._1c_syntax.bsl.mdclasses.CF;
import com.github._1c_syntax.bsl.mdclasses.MDClasses;
import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.mdo.ModuleOwner;
import com.github._1c_syntax.bsl.mdo.Subsystem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.helpers.BslFilter;
import ru.alkoleft.bsl.doc.bsl.helpers.ModuleContextBuilder;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BslContext {
  @Getter
  private static BslContext current;
  @Getter
  private final CF configuration;
  private final ModuleContextBuilder builder;
  private List<ModuleInfo> modules = Collections.emptyList();

  public BslContext(Path path, Filter filter) {
    BslFilter.setFilter(filter);
    builder = new ModuleContextBuilder();
    configuration = (CF) MDClasses.createConfiguration(path);
    current = this;
  }

  public Stream<Module> getModules() {
    return configuration.getChildren().stream()
        .filter(ModuleOwner.class::isInstance)
        .map(ModuleOwner.class::cast)
        .flatMap(it -> it.getModules().stream())
        .filter(BslFilter::checkModule);
  }

  public ModuleInfo getModuleContext(Module module) {
    return builder.buildFilteredModuleContext(module);
  }

  public Stream<Subsystem> getRootSubsystems(boolean filtered) {
    var stream = configuration.getSubsystems().stream();

    if (filtered) {
      stream = stream.filter(BslFilter::checkRootSubsystem);
    }
    return stream;
  }

  public Stream<Subsystem> getChildrenSubsystems(Subsystem parent) {
    return parent.getSubsystems().stream();
  }

  public boolean contains(String name) {
    return modules.stream().anyMatch(it -> it.getName().equalsIgnoreCase(name));
  }

  public Optional<ModuleInfo> getModule(String name) {
    return modules.stream().filter(it -> it.getName().equalsIgnoreCase(name)).findAny();
  }

  public Stream<MD> getSubsystemObjects(Subsystem subsystem) {
    return subsystem.getContent().stream()
        .map(configuration::findChild)
        .filter(Optional::isPresent)
        .map(Optional::get);
  }

  public void load() {
    modules = getModules()
        .map(builder::buildModuleContext)
        .collect(Collectors.toList());
  }

  public MethodInfo getMethodInfo(String link) {
    if (link == null || link.isEmpty()) {
      return null;
    }
    var linkInfo = Links.parseLink(link, false);
    return getMethodInfo(linkInfo);
  }

  public MethodInfo getMethodInfo(Links.Link link) {

    if (link == null || link.ownerName() == null || link.methodName() == null) {
      return null;
    }

    var module = getModule(link.ownerName());
    var method = module.flatMap(it -> it.getMethod(link.methodName()));

    return MethodInfo.builder()
        .module(module.orElse(null))
        .method(method.orElse(null))
        .publishing(module.isPresent()
            && method.isPresent()
            && BslFilter.checkModule(module.get())
            && BslFilter.checkMethod(method.get()))
        .build();
  }

}
