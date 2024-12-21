package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserBaseVisitor;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import com.github._1c_syntax.bsl.parser.description.BSLDescriptionReader;
import com.github._1c_syntax.bsl.parser.description.MethodDescription;
import com.github._1c_syntax.bsl.parser.description.support.SimpleRange;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

@Slf4j
public class MethodSymbolComputer extends BSLParserBaseVisitor<ParseTree> {
  private final List<MethodSymbol> methods = new ArrayList<>();
  List<RegionData> regionData = new ArrayList<>();
  Stack<RegionData> regions = new Stack<>();
  private BSLTokenizer tokenizer;

  private static List<ParameterDefinition> createParameters(BSLParser.ParamListContext paramList,
                                                            Optional<MethodDescription> description) {
    if (paramList == null) {
      return Collections.emptyList();
    }

    return paramList.param().stream()
        .map((BSLParser.ParamContext param) -> {
          String parameterName = getParameterName(param.IDENTIFIER());
          return ParameterDefinition.builder()
              .name(parameterName)
              .byValue(param.VAL_KEYWORD() != null)
              .defaultValue(getDefaultValue(param))
              .description(getParameterDescription(parameterName, description))
              .build();
        }).collect(Collectors.toList());
  }

  private static ParameterDefinition.DefaultValue getDefaultValue(BSLParser.ParamContext param) {
    if (param.defaultValue() == null) {
      return ParameterDefinition.DefaultValue.EMPTY;
    }

    var constValue = param.defaultValue().constValue();

    ParameterDefinition.DefaultValue defaultValue;
    if (constValue.DATETIME() != null) {
      var value = constValue.DATETIME().getSymbol().getText();
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.DATETIME, value.intern());
    } else if (constValue.FALSE() != null) {
      var value = constValue.FALSE().getSymbol().getText();
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.BOOLEAN, value.intern());
    } else if (constValue.TRUE() != null) {
      var value = constValue.TRUE().getSymbol().getText();
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.BOOLEAN, value.intern());
    } else if (constValue.UNDEFINED() != null) {
      var value = constValue.UNDEFINED().getSymbol().getText();
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.UNDEFINED, value.intern());
    } else if (constValue.NULL() != null) {
      var value = constValue.NULL().getSymbol().getText();
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.NULL, value.intern());
    } else if (constValue.string() != null) {
      var value = constValue.string().STRING().stream()
          .map(TerminalNode::getSymbol)
          .map(Token::getText)
          .collect(Collectors.joining("\n"));
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.STRING, value.intern());
    } else if (constValue.numeric() != null) {
      var value = constValue.numeric().getText();
      if (constValue.MINUS() != null) {
        value = constValue.MINUS().getSymbol().getText() + value;
      }
      if (constValue.PLUS() != null) {
        value = constValue.PLUS().getSymbol().getText() + value;
      }
      defaultValue = new ParameterDefinition.DefaultValue(ParameterDefinition.ParameterType.NUMERIC, value.intern());
    } else {
      defaultValue = ParameterDefinition.DefaultValue.EMPTY;
    }

    return defaultValue;
  }

  private static String getParameterName(TerminalNode identifier) {
    return Optional.ofNullable(identifier)
        .map(ParseTree::getText)
        .map(String::intern)
        .orElse("<UNKNOWN_IDENTIFIER>");
  }

  private static Optional<ParameterDescription> getParameterDescription(String parameterName,
                                                                        Optional<MethodDescription> description) {

    return description.map(MethodDescription::getParameters)
        .stream()
        .flatMap(Collection::stream)
        .filter(parameterDescription -> parameterDescription.getName().equalsIgnoreCase(parameterName))
        .findFirst()
        .map(p -> new ParameterDescription(p.getName(), p.getTypes(), p.getLink(), p.isHyperlink()));

  }

  public List<MethodSymbol> compute(BSLTokenizer tokenizer) {
    this.tokenizer = tokenizer;
    methods.clear();
    visitFile(tokenizer.getAst());

    // Используется постпривязка регионов, тк порядок обхода не соответствует порядку следования объект (в метод заходит раньше региона)
    for (var region : regionData) {
      for (var method : methods) {
        if (SimpleRange.containsRange(region.range, method.getRange())) {
          method.setRegion(region.region);
        }
      }
    }
    return methods;
  }

  @Override
  public ParseTree visitRegionStart(BSLParser.RegionStartContext ctx) {
    var name = ctx.regionName().getText();

    log.debug("Start region: " + name);

    var data = new RegionData();
    data.start = ctx;
    if (regions.isEmpty()) {
      data.region = new RegionSymbol(name, null);
    } else {
      data.region = new RegionSymbol(name, regions.peek().region);
    }
    regions.push(data);
    regionData.add(data);
    return super.visitRegionStart(ctx);
  }

  @Override
  public ParseTree visitRegionEnd(BSLParser.RegionEndContext ctx) {
    if (!regions.isEmpty()) {
      var regionData = regions.pop();
      log.debug("End region: " + regionData.region.getName());
      regionData.end = ctx;
      regionData.range = SimpleRange.create(regionData.start.start, regionData.end.stop);
    }
    return super.visitRegionEnd(ctx);
  }

  @Override
  public ParseTree visitFunction(BSLParser.FunctionContext ctx) {
    var declaration = ctx.funcDeclaration();
    log.debug("Function: " + declaration.subName().getText());
    var startNode = declaration.FUNCTION_KEYWORD();
    handleMethod(startNode, declaration.subName().getStart(), declaration.paramList(), true, declaration.EXPORT_KEYWORD() != null);
    return super.visitFunction(ctx);
  }

  @Override
  public ParseTree visitProcedure(BSLParser.ProcedureContext ctx) {
    var declaration = ctx.procDeclaration();
    log.debug("Procedure: " + declaration.subName().getText());

    TerminalNode startNode = declaration.PROCEDURE_KEYWORD();
    handleMethod(startNode, declaration.subName().getStart(), declaration.paramList(), false, declaration.EXPORT_KEYWORD() != null);
    return super.visitProcedure(ctx);
  }

  private void handleMethod(TerminalNode startNode, Token subName, BSLParser.ParamListContext paramList, boolean function, boolean export) {
    var description = createDescription(startNode.getSymbol());
    var deprecated = description
        .map(MethodDescription::isDeprecated)
        .orElse(false);

    var method = MethodSymbol.builder()
        .name(subName.getText().intern())
        .function(function)
        .export(export)
        .fullDescription(description)
        .deprecated(deprecated)
        .parameters(createParameters(paramList, description))
        .range(SimpleRange.create(startNode.getSymbol(), startNode.getSymbol()))
        .build();
    methods.add(method);
  }

  private Optional<MethodDescription> createDescription(Token token) {
    List<Token> comments = Trees.getComments(tokenizer.getTokens(), token);
    if (comments.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(BSLDescriptionReader.parseMethodDescription(comments));
  }

  private static class RegionData {
    RegionSymbol region;
    BSLParser.RegionStartContext start;
    BSLParser.RegionEndContext end;
    SimpleRange range;
  }
}
