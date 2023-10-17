package ru.alkoleft.bsl.doc.bsl;

import com.github._1c_syntax.bsl.types.MDOType;
import com.github._1c_syntax.mdclasses.Configuration;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;
import com.google.common.base.Strings;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.helpers.BslFilter;
import ru.alkoleft.bsl.doc.bsl.helpers.ModuleContextBuilder;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BslContext {
  @Getter
  private static BslContext current;
  @Getter
  private final Configuration configuration;
  private final Set<MDOType> topObjectsType = Set.copyOf(MDOType.valuesWithoutChildren());
  private final ModuleContextBuilder builder;
  private List<ModuleInfo> modules = Collections.emptyList();

  public BslContext(Path path, Filter filter) {
    BslFilter.setFilter(filter);
    builder = new ModuleContextBuilder();
    configuration = Configuration.create(path);
    current = this;
  }

  public Stream<MDOModule> getModules() {
    return configuration.getChildren().stream()
        .filter(AbstractMDObjectBSL.class::isInstance)
        .map(AbstractMDObjectBSL.class::cast)
        .flatMap(it -> it.getModules().stream())
        .filter(BslFilter::checkModule);
  }

  public ModuleInfo getModuleContext(MDOModule module) {
    return builder.buildFilteredModuleContext(module);
  }

  public Stream<MDSubsystem> getRootSubsystems(boolean filtered) {
    var stream = configuration.getOrderedTopMDObjects().get(MDOType.SUBSYSTEM)
        .stream()
        .map(MDSubsystem.class::cast);

    if (filtered) {
      stream = stream.filter(BslFilter::checkRootSubsystem);
    }
    return stream;
  }

  public Stream<MDSubsystem> getChildrenSubsystems(MDSubsystem parent) {
    return parent.getChildren().stream()
        .filter(Either::isRight)
        .map(Either::get)
        .filter(MDSubsystem.class::isInstance)
        .map(MDSubsystem.class::cast);
  }

  public boolean contains(String name) {
    return modules.stream().anyMatch(it -> it.getName().equalsIgnoreCase(name));
  }

  public Optional<ModuleInfo> getModule(String name) {
    return modules.stream().filter(it -> it.getName().equalsIgnoreCase(name)).findAny();
  }

  public Stream<AbstractMDObjectBSL> getSubsystemObjects(MDSubsystem subsystem) {

    return configuration.getChildren()
        .stream()
        .filter(AbstractMDObjectBSL.class::isInstance)
        .filter(this::isTopObject)
        .filter(it -> it.getIncludedSubsystems().contains(subsystem))
        .map(AbstractMDObjectBSL.class::cast);
  }

  boolean isTopObject(AbstractMDObjectBase obj) {
    return topObjectsType.contains(obj.getMdoType());
  }

  public void load() {
    modules = getModules()
        .map(builder::buildModuleContext)
        .collect(Collectors.toList());
  }

  public MethodInfo getMethodInfo(String link) {
    if (Strings.isNullOrEmpty(link)) {
      return null;
    }
    var linkInfo = Links.parseLink(link, false);
    return getMethodInfo(linkInfo);
  }

  public MethodInfo getMethodInfo(Links.Link link) {

    if (link == null || link.getOwnerName() == null || link.getMethodName() == null) {
      return null;
    }

    var module = getModule(link.getOwnerName());
    var method = module.flatMap(it -> it.getMethod(link.getMethodName()));

    return MethodInfo.builder()
        .module(module.orElse(null))
        .method(method.orElse(null))
        .publishing(module.isPresent() && method.isPresent() && BslFilter.checkModule(module.get()) && BslFilter.checkMethod(method.get()))
        .build();
  }

}
