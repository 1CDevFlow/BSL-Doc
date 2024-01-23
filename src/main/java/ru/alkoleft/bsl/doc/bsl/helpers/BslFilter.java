package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.mdo.Subsystem;
import com.github._1c_syntax.bsl.types.ModuleType;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.Filter;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.Set;
import java.util.stream.Stream;

@UtilityClass
public class BslFilter {

  private final Set<ModuleType> moduleTypes = Set.of(
    ModuleType.CommonModule,
    ModuleType.ValueManagerModule,
    ModuleType.ManagerModule
  );

  @Getter
  private Filter filter;

  public static void setFilter(Filter filter) {
    BslFilter.filter = filter;
  }

  public boolean checkMethod(MethodSymbol method) {
    return (!filter.isExport() || method.isExport())
      && (filter.getRegions().isEmpty() || checkRegion(method));
  }

  public Stream<MethodSymbol> setFilter(Stream<MethodSymbol> stream) {
    if (filter.isExport()) {
      stream = stream.filter(MethodSymbol::isExport);
    }

    if (!filter.getRegions().isEmpty()) {
      stream = stream.filter(BslFilter::checkRegion);
    }

    return stream;
  }

  public boolean checkModule(ModuleInfo module) {
    return true;
  }

  public boolean checkModule(Module module) {
    return !filter.isOnlyCommonAndManagerModules() || moduleTypes.contains(module.getModuleType());
  }

  public boolean checkRegion(MethodSymbol m) {
    var region = m.getRegion();

    while (region != null) {
      if (filter.getRegions().contains(region.getName())) {
        return true;
      }
      region = region.getParent();
    }
    return false;
  }

  public boolean checkRootSubsystem(Subsystem subsystem) {
    if (filter.getRootSubsystems().isEmpty()) {
      return true;
    } else {
      return filter.getRootSubsystems().contains(subsystem.getName());
    }
  }
}
