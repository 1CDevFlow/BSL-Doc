package ru.alkoleft.bsl.doc.bsl;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;

import java.util.List;

@Builder
@Value
public class ModuleContext {
  AbstractMDObjectBSL owner;
  List<MethodSymbol> methods;
  String description;

  public String getName() {
    return owner.getName();
  }

  public boolean isNotEmpty() {
    return methods != null && !methods.isEmpty();
  }
}
