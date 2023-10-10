package ru.alkoleft.bsl.doc.bsl;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;
import lombok.Builder;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.List;
import java.util.Optional;

@Builder
@Value
public class ModuleInfo {
  AbstractMDObjectBSL owner;
  MDOModule module;
  List<MethodSymbol> methods;
  String description;

  public String getName() {
    return owner.getName();
  }

  public boolean isNotEmpty() {
    return methods != null && !methods.isEmpty();
  }

  public boolean isEmpty() {
    return methods == null || methods.isEmpty();
  }

  public Optional<MethodSymbol> getMethod(String name) {
    return getMethods().stream().filter(it -> name.equalsIgnoreCase(it.getName())).findAny();
  }
}
