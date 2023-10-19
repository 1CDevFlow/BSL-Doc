/*
 * This file is a part of BSL Language Server.
 *
 * Copyright (c) 2018-2023
 * Alexey Sosnoviy <labotamy@gmail.com>, Nikita Fedkin <nixel2007@gmail.com> and contributors
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * BSL Language Server is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * BSL Language Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BSL Language Server.
 */

// from https://github.com/1c-syntax/bsl-language-server/blob/develop/src/main/java/com/github/_1c_syntax/bsl/languageserver/utils/Trees.java

package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.parser.BSLParser;
import lombok.experimental.UtilityClass;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class Trees {
  private static final Set<Integer> VALID_TOKEN_TYPES_FOR_COMMENTS_SEARCH = Set.of(
      BSLParser.ANNOTATION_ATCLIENT_SYMBOL,
      BSLParser.ANNOTATION_ATSERVERNOCONTEXT_SYMBOL,
      BSLParser.ANNOTATION_ATCLIENTATSERVERNOCONTEXT_SYMBOL,
      BSLParser.ANNOTATION_ATCLIENTATSERVER_SYMBOL,
      BSLParser.ANNOTATION_ATSERVER_SYMBOL,
      BSLParser.ANNOTATION_CUSTOM_SYMBOL,
      BSLParser.ANNOTATION_UNKNOWN,
      BSLParser.LINE_COMMENT,
      BSLParser.WHITE_SPACE,
      BSLParser.AMPERSAND
  );

  /**
   * Поиск комментариев назад от указанного токена
   *
   * @param tokens - список токенов DocumentContext
   * @param token  - токен, для которого требуется найти комментарии
   * @return - список найденных комментариев lines
   */
  public static List<Token> getComments(List<Token> tokens, Token token) {
    List<Token> comments = new ArrayList<>();
    fillCommentsCollection(tokens, token, comments);
    return comments.stream()
        .filter(it -> {
          var text = it.getText();
          return !text.startsWith("// @skip") && !text.startsWith("// BSLLS:");
        })
        .collect(Collectors.toList());
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
}
