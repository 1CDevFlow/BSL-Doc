package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.parser.description.support.ParameterDescription;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

/**
 * Описание типа параметра, прочитанного из описания метода
 */
@AllArgsConstructor
@Value
public class TypeDescription {
  /**
   * Имя типа. На данный момент может быть строковый массив перечисления типов а также гиперссылка на метод
   */
  String name;
  /**
   * Описание типа. Может быть пустым
   */
  String description;
  /**
   * Параметры (ключи или поля) типа для сложных типов данных. Может быть пустым
   */
  List<ParameterDescription> parameters;
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
