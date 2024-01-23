package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
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
  public ModuleInfo buildModuleContext(Module bslModule) {
    var owner = MDOHelper.getOwner(bslModule);
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

  public ModuleInfo buildFilteredModuleContext(Module bslModule) {
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
