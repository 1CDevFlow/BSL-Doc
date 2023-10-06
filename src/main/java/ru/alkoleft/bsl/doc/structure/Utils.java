package ru.alkoleft.bsl.doc.structure;

import com.github._1c_syntax.bsl.types.ModuleType;
import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class Utils {

  Set<ModuleType> moduleTypes = Set.of(
      ModuleType.CommonModule,
      ModuleType.ValueManagerModule,
      ModuleType.ManagerModule
  );

  public boolean isManagerModule(MDOModule module) {
    return moduleTypes.contains(module.getModuleType());
  }
}
