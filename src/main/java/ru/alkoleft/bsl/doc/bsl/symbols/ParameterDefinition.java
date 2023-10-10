package ru.alkoleft.bsl.doc.bsl.symbols;

import com.github._1c_syntax.bsl.languageserver.context.symbol.description.TypeDescription;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Value
@Builder
public class ParameterDefinition {
  String name;
  Optional<ParameterDescription> description;
  String type;
  DefaultValue defaultValue;
  boolean byValue;

  public String getDescription() {
    if (description.isEmpty()) {
      return "";
    }
    return description.get().getTypes()
        .stream()
        .findFirst()
        .map(TypeDescription::getDescription).orElse("");
  }

  public List<TypeDescription> getTypes() {
    if (description.isEmpty()) {
      return Collections.emptyList();
    } else {
      return description.get().getTypes();
    }
  }

  public boolean isRequired() {
    return defaultValue.type == ParameterType.EMPTY;
  }

  public enum ParameterType {
    DATETIME,
    BOOLEAN,
    UNDEFINED,
    NULL,
    STRING,
    NUMERIC,
    EMPTY
  }

  @Value
  public static class DefaultValue {
    public static final DefaultValue EMPTY = new DefaultValue(ParameterType.EMPTY, "");

    ParameterType type;
    String value;
  }

}
