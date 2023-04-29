package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.languageserver.context.symbol.description.MethodDescription;
import com.github._1c_syntax.bsl.languageserver.utils.Ranges;
import com.github._1c_syntax.bsl.languageserver.utils.Trees;
import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLParserBaseVisitor;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class MethodSymbolComputer extends BSLParserBaseVisitor<ParseTree> {
  private final List<MethodSymbol> methods = new ArrayList<>();
  private BSLTokenizer tokenizer;

  Stack<RegionSymbol> regions = new Stack<>();

  public List<MethodSymbol> compute(BSLTokenizer tokenizer) {
    this.tokenizer = tokenizer;
    methods.clear();
    visitFile(tokenizer.getAst());
    return methods;
  }

  @Override
  public ParseTree visitRegionStart(BSLParser.RegionStartContext ctx) {
    var name = ctx.regionName().getText();

    RegionSymbol region;

    if (regions.isEmpty()) {
      region = new RegionSymbol(name, null);
    } else {
      region = new RegionSymbol(name, regions.peek());
    }
    regions.push(region);
    return super.visitRegionStart(ctx);
  }

  @Override
  public ParseTree visitRegionEnd(BSLParser.RegionEndContext ctx) {
    regions.pop();
    return super.visitRegionEnd(ctx);
  }

  @Override
  public ParseTree visitFunction(BSLParser.FunctionContext ctx) {
    BSLParser.FuncDeclarationContext declaration = ctx.funcDeclaration();

    TerminalNode startNode = declaration.FUNCTION_KEYWORD();
    handleMethod(startNode, declaration.subName().getStart(), declaration.paramList(), true, declaration.EXPORT_KEYWORD() != null);
    return super.visitFunction(ctx);
  }

  @Override
  public ParseTree visitProcedure(BSLParser.ProcedureContext ctx) {
    BSLParser.ProcDeclarationContext declaration = ctx.procDeclaration();

    TerminalNode startNode = declaration.PROCEDURE_KEYWORD();
    handleMethod(startNode, declaration.subName().getStart(), declaration.paramList(), false, declaration.EXPORT_KEYWORD() != null);
    return super.visitProcedure(ctx);
  }

  private void handleMethod(TerminalNode startNode, Token subName, BSLParser.ParamListContext paramList, boolean function, boolean export) {
    Optional<MethodDescription> description = createDescription(startNode.getSymbol());
    boolean deprecated = description
        .map(MethodDescription::isDeprecated)
        .orElse(false);

    var method = MethodSymbol.builder()
        .name(subName.getText().intern())
        .function(function)
        .export(export)
        .fullDescription(description)
        .deprecated(deprecated)
        .parameters(createParameters(paramList, description))
        .region(regions.peek())
        .build();
    methods.add(method);
  }

  private static List<ParameterDefinition> createParameters(BSLParser.ParamListContext paramList, Optional<MethodDescription> description) {
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

  private static Optional<ParameterDescription> getParameterDescription(String parameterName, Optional<MethodDescription> description) {

    return description.map(MethodDescription::getParameters)
        .stream()
        .flatMap(Collection::stream)
        .filter(parameterDescription -> parameterDescription.getName().equalsIgnoreCase(parameterName))
        .findFirst()
        .map(p -> new ParameterDescription(p.getName(), p.getTypes(), p.getLink(), p.isHyperlink()));

  }

  private Optional<MethodDescription> createDescription(Token token) {
    List<Token> comments = Trees.getComments(tokenizer.getTokens(), token);
    if (comments.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(new MethodDescription(comments));
  }
}
