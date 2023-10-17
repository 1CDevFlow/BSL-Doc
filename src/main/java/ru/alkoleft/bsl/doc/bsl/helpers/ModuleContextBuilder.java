package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;
import lombok.extern.slf4j.Slf4j;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbolComputer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ModuleContextBuilder {
  public ModuleInfo buildModuleContext(MDOModule bslModule) {
    var owner = (AbstractMDObjectBSL) bslModule.getOwner();
    log.debug("Parse module: " + owner.getName() + "." + bslModule.getModuleType());
    var srcPath = Path.of(bslModule.getUri());
    List<MethodSymbol> methods;
    String description;
    try {
      var content = Files.readString(srcPath);
      MethodSymbolComputer computer = new MethodSymbolComputer();
      var tokenizer = new BSLTokenizer(content);
      methods = computer.compute(tokenizer);
      description = ModuleComputer.computeModuleDescription(tokenizer);
    } catch (Exception e) {
      throw new RuntimeException(owner.getMdoReference().getMdoRef() + ". Module parsing error", e);
    }

    return ModuleInfo.builder()
        .owner(owner)
        .module(bslModule)
        .methods(methods)
        .description(description)
        .build();
  }

  public ModuleInfo buildFilteredModuleContext(MDOModule bslModule) {
    return buildModuleContext(buildModuleContext(bslModule));
  }

  public ModuleInfo buildModuleContext(ModuleInfo module) {

    var stream = BslFilter.setFilter(module.getMethods().stream());

    return ModuleInfo.builder()
        .module(module.getModule())
        .owner(module.getOwner())
        .description(module.getDescription())
        .methods(stream.collect(Collectors.toList()))
        .build();
  }

}
