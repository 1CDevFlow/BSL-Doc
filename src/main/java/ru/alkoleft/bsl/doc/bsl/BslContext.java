package ru.alkoleft.bsl.doc.bsl;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import com.github._1c_syntax.bsl.types.MDOType;
import com.github._1c_syntax.mdclasses.Configuration;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBSL;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import com.github._1c_syntax.mdclasses.mdo.MDCommonModule;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import com.github._1c_syntax.mdclasses.mdo.support.MDOModule;
import com.google.common.base.Strings;
import io.vavr.control.Either;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbol;
import ru.alkoleft.bsl.doc.bsl.symbols.MethodSymbolComputer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
  Configuration configuration;

  @Getter
  Filter filter;

  List<ModuleContext> modules = Collections.emptyList();

  Set<MDOType> topObjectsType = Set.of(
      MDOType.COMMON_MODULE,
      MDOType.ENUM,
      MDOType.CATALOG,
      MDOType.DOCUMENT,
      MDOType.ACCOUNTING_REGISTER,
      MDOType.ACCUMULATION_REGISTER,
      MDOType.CALCULATION_REGISTER,
      MDOType.INFORMATION_REGISTER
  );

  private static final Set<Integer> VALID_TOKEN_TYPES_FOR_COMMENTS_SEARCH = Set.of(
      BSLParser.LINE_COMMENT,
      BSLParser.WHITE_SPACE,
      BSLParser.AMPERSAND
  );

  public BslContext(Path path, Filter filter) {
    this.filter = filter;
    configuration = Configuration.create(path);
    current = this;
  }

  public Stream<ModuleContext> getModules() {
    var stream = modules.stream();

    if (!filter.getModules().isEmpty()) {
      stream = stream.filter(m -> filter.getModules().contains(m.getName()));
    }

    return stream
        .map(this::buildModuleContext)
        .filter(ModuleContext::isNotEmpty);
  }

  public Stream<MDSubsystem> getRootSubsystems() {
    return configuration.getOrderedTopMDObjects().get(MDOType.SUBSYSTEM).stream()
        .map(MDSubsystem.class::cast);
  }

  public Stream<MDSubsystem> getChildrenSubsystems(MDSubsystem parent) {
    return parent.getChildren().stream()
        .filter(Either::isRight)
        .map(Either::get)
        .filter(MDSubsystem.class::isInstance)
        .map(MDSubsystem.class::cast);
  }

  public Stream<MDSubsystem> getSubsystems() {
    return getRootSubsystems()
        .flatMap(this::getRecursiveChildrenSubsystems);
  }

  private Stream<MDSubsystem> getRecursiveChildrenSubsystems(MDSubsystem parent) {
    if (parent.getChildren().isEmpty()) {
      return Stream.of(parent);
    }

    return Stream.concat(Stream.of(parent), parent.getChildren().stream()
        .filter(Either::isRight)
        .map(Either::get)
        .filter(MDSubsystem.class::isInstance)
        .map(MDSubsystem.class::cast)
        .flatMap(this::getRecursiveChildrenSubsystems));
  }

  public boolean contains(String name) {
    return modules.stream().anyMatch(it -> it.getName().equalsIgnoreCase(name));
  }

  public Optional<ModuleContext> getModule(String name) {
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
    modules = configuration.getOrderedTopMDObjects().get(MDOType.COMMON_MODULE).stream()
        .map(MDCommonModule.class::cast)
        .map(this::buildModuleContext)
        .collect(Collectors.toList());
  }

  public ModuleContext buildModuleContext(MDCommonModule module) {
    var bslModules = module.getModules();
    var bslModule = bslModules.get(0);
    return buildModuleContext(bslModule);
  }

  public ModuleContext buildFilteredModuleContext(MDOModule bslModule) {
    return buildModuleContext(buildModuleContext(bslModule));
  }
  public ModuleContext buildModuleContext(MDOModule bslModule) {
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
      description = computeModuleDescription(tokenizer);
    } catch (Exception e) {
      throw new RuntimeException(owner.getMdoReference().getMdoRef() + ". Module parsing error", e);
    }

    return ModuleContext.builder()
        .owner(owner)
        .module(bslModule)
        .methods(methods)
        .description(description)
        .build();
  }

  private String computeModuleDescription(BSLTokenizer tokenizer) {
    var token = tokenizer.getAst().getStart();

    var isDescription = false;
    List<Token> comments = null;
    while (token != null && !isDescription) {
      comments = getComments(tokenizer.getTokens(), token);
      isDescription = !comments.isEmpty() && comments.stream().map(Token::getText).map(it -> it.substring(2)).map(String::trim).noneMatch(it -> it.contains("Copyright") || it.contains("Экспортные"));
      token = comments.isEmpty() ? null : comments.get(0);
    }
    if (isDescription) {
      var lines = comments.stream()
          .map(Token::getText)
          .map(it -> it.substring(2))
          .filter(it -> it.isEmpty() || !it.matches("/+"))
          .collect(Collectors.toList());

      var chars = lines.get(0).toCharArray();

      for (int i = 1; i < lines.size(); i++) {
        var line = lines.get(i);

        for (int j = 0; j < chars.length; j++) {
          if (line.length() <= j || line.charAt(j) != chars[j]) {
            chars = Arrays.copyOf(chars, j);
            break;
          }
        }
        if (chars.length == 0) {
          break;
        }
      }

      if (chars.length > 0) {
        char[] finalChars = chars;
        return lines.stream()
            .map(it -> it.substring(finalChars.length))
            .collect(Collectors.joining("\n"));
      } else {
        return String.join("\n", lines);
      }
    } else {
      return "";
    }
  }

  private ModuleContext buildModuleContext(ModuleContext module) {

    var stream = module.getMethods().stream();

    if (filter.isExport()) {
      stream = stream.filter(MethodSymbol::isExport);
    }

    if (!filter.getRegions().isEmpty()) {
      stream = stream.filter(this::regionFilter);
    }

    return ModuleContext.builder()
        .owner(module.getOwner())
        .methods(stream.collect(Collectors.toList()))
        .description(module.getDescription())
        .build();
  }

  private boolean checkModule(ModuleContext module) {
    return true;
  }

  private boolean checkMethod(MethodSymbol method) {
    return (!filter.isExport() || method.isExport())
        && (filter.getRegions().isEmpty() || regionFilter(method));
  }

  private boolean regionFilter(MethodSymbol m) {
    var region = m.getRegion();

    while (region != null) {
      if (filter.getRegions().contains(region.getName())) {
        return true;
      }
      region = region.getParent();
    }
    return false;
  }

  public static List<Token> getComments(List<Token> tokens, Token token) {
    List<Token> comments = new ArrayList<>();
    fillCommentsCollection(tokens, token, comments);
    return comments;
  }

  private static void fillCommentsCollection(List<Token> tokens, Token currentToken, List<Token> lines) {

    int index = currentToken.getTokenIndex();

    if (index == 0) {
      return;
    }

    var previousToken = tokens.get(index - 1);

    if (abortSearchComments(previousToken, currentToken)) {
      return;
    }

    fillCommentsCollection(tokens, previousToken, lines);
    int type = previousToken.getType();
    if (type == BSLParser.LINE_COMMENT) {
      lines.add(previousToken);
    }
  }

  private static boolean abortSearchComments(Token previousToken, Token currentToken) {
    int type = previousToken.getType();
    return !VALID_TOKEN_TYPES_FOR_COMMENTS_SEARCH.contains(type) || isBlankLine(previousToken, currentToken);
  }

  private static boolean isBlankLine(Token previousToken, Token currentToken) {
    return previousToken.getType() == BSLParser.WHITE_SPACE
        && (previousToken.getTokenIndex() == 0
        || (previousToken.getLine() + 1) != currentToken.getLine());
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
        .publishing(module.isPresent() && method.isPresent() && checkModule(module.get()) && checkMethod(method.get()))
        .build();
  }
}
