package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.parser.BSLTokenizer;
import lombok.experimental.UtilityClass;
import org.antlr.v4.runtime.Token;
import ru.alkoleft.bsl.doc.bsl.symbols.Trees;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ModuleComputer {

  public String computeModuleDescription(BSLTokenizer tokenizer) {
    var token = tokenizer.getAst().getStart();

    var isDescription = false;
    List<Token> comments = null;
    while (token != null && !isDescription) {
      comments = Trees.getComments(tokenizer.getTokens(), token);
      isDescription = !comments.isEmpty()
          && comments.stream()
          .map(Token::getText)
          .map(it -> it.substring(2))
          .map(String::trim)
          .noneMatch(it -> it.contains("Copyright") || it.contains("Экспортные"));
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
}
