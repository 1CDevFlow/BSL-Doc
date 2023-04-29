package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.languageserver.context.symbol.description.TypeDescription;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * Описание параметра из комментария - описания метода
 */
@AllArgsConstructor
@Value
public class ParameterDescription {
  /**
   * Имя параметра
   */
  String name;
  /**
   * Возможные типы параметра. Может быть пустым
   */
  List<TypeDescription> types;
  /**
   * Если описание параметров содержит только ссылку, то здесь будет ее значение
   * <p>
   * TODO Временное решение, надо будет продумать в следующем релизе
   */
  String link;
  /**
   * Признак того, что параметр является гиперссылкой
   */
  boolean isHyperlink;

}