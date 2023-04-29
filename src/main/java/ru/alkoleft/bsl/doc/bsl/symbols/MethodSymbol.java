package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.languageserver.context.symbol.description.MethodDescription;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Builder
@Value
public class MethodSymbol {
  boolean deprecated;
  boolean function;
  boolean export;
  String name;
  Optional<MethodDescription> fullDescription;
  List<ParameterDefinition> parameters;
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
      return new TypeDescription(result.getName(), result.getDescription(), result.getParameters(), Strings.isNullOrEmpty(result.getLink()) ? null : "см. " + result.getLink(), result.isHyperlink());
    } else {
      return null;
    }
  }
}
