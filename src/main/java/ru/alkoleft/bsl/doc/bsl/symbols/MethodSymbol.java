package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.languageserver.context.symbol.description.MethodDescription;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp4j.Range;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
@Value
@Slf4j
public class MethodSymbol {
  boolean deprecated;
  boolean function;
  boolean export;
  String name;
  Optional<MethodDescription> fullDescription;

  List<ParameterDefinition> parameters;
  Range range;
  @Setter
  @NonFinal
  RegionSymbol region;

  public String getDescription() {
    return fullDescription.map(MethodDescription::getPurposeDescription).orElse("");
  }

  public List<String> getExamples() {
    return fullDescription.map(MethodDescription::getExamples).orElse(Collections.emptyList());
  }

  public TypeDescription getReturnedValue() {
    var result = fullDescription.map(MethodDescription::getReturnedValue).filter(it -> !it.isEmpty()).map(it -> it.get(0)).orElse(null);

    if (result != null) {
      return createTypeDescription(result);
    } else {
      return null;
    }
  }

  private TypeDescription createTypeDescription(com.github._1c_syntax.bsl.languageserver.context.symbol.description.TypeDescription baseDescription) {
    TypeDescription result = null;
    if (!Strings.isNullOrEmpty(baseDescription.getLink())) {
      var methodInfo = BslContext.getCurrent().getMethodInfo(baseDescription.getLink());

      if (methodInfo != null && methodInfo.getMethod() != null) {
        result = methodInfo.getMethod().getReturnedValue();
      } else {
        log.warn("Bad link: " + baseDescription.getLink());
      }
    }
    if (result == null) {
      result = new TypeDescription(baseDescription.getName(), baseDescription.getDescription(), baseDescription.getParameters(), Strings.isNullOrEmpty(baseDescription.getLink()) ? null : "см. " + baseDescription.getLink(), baseDescription.isHyperlink());
    }
    return result;
  }

  public String callExample() {
    var builder = new StringBuilder();
    if (function) {
      builder.append("Функция");
    } else {
      builder.append("Процедура");
    }
    builder.append(" ").append(name).append("(");
    boolean firstParameter = true;
    for (var parameter : parameters) {
      if (firstParameter) {
        firstParameter = false;
      } else {
        builder.append(", ");
      }
      if (parameter.isByValue()) {
        builder.append("Знач ");
      }
      builder.append(parameter.getName());
      if (parameter.getDefaultValue().getType() != ParameterDefinition.ParameterType.EMPTY) {
        builder.append(" = ").append(parameter.getDefaultValue().getValue());
      }
    }
    builder.append(")");
    if (isExport()) {
      builder.append(" Экспорт");
    }

    return builder.toString();
  }
}
