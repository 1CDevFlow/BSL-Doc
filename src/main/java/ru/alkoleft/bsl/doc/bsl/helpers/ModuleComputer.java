package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.parser.BSLParser;
import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import lombok.experimental.UtilityClass;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ModuleComputer {

  private final Set<Integer> VALID_TOKEN_TYPES_FOR_COMMENTS_SEARCH = Set.of(
      BSLParser.LINE_COMMENT,
      BSLParser.WHITE_SPACE,
      BSLParser.AMPERSAND
  );

  public String computeModuleDescription(BSLTokenizer tokenizer) {
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

  private List<Token> getComments(List<Token> tokens, Token token) {
    List<Token> comments = new ArrayList<>();
    fillCommentsCollection(tokens, token, comments);
    return comments;
  }

  private void fillCommentsCollection(List<Token> tokens, Token currentToken, List<Token> lines) {

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

  private boolean abortSearchComments(Token previousToken, Token currentToken) {
    int type = previousToken.getType();
    return !VALID_TOKEN_TYPES_FOR_COMMENTS_SEARCH.contains(type) || isBlankLine(previousToken, currentToken);
  }

  private boolean isBlankLine(Token previousToken, Token currentToken) {
    return previousToken.getType() == BSLParser.WHITE_SPACE
        && (previousToken.getTokenIndex() == 0
        || (previousToken.getLine() + 1) != currentToken.getLine());
  }


}
