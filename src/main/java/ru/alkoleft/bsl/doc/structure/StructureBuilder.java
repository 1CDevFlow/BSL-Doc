package ru.alkoleft.bsl.doc.structure;

import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.options.OutputHierarchy;
import ru.alkoleft.bsl.doc.options.OutputOptions;

import java.util.List;
import java.util.Objects;

public interface StructureBuilder {

  List<Item> build(BslContext context);

  static StructureBuilder builder(OutputOptions options) {
    if (Objects.requireNonNull(options.getHierarchy()) == OutputHierarchy.SUBSYSTEM) {
      return new SubsystemsStructureBuilder();
    }
    return new FlatStructureBuilder();
  }

  static void print(List<Item> structure) {
    print(structure, "");
  }

  private static void print(List<Item> structure, String prefix) {
    Item item;
    for (int index = 0; index < structure.size(); index++) {
      item = structure.get(index);
      if (index == structure.size() - 1) {
        System.out.printf("%s└── %s\n", prefix, item.getPresent());
        if (!item.getChildren().isEmpty()) {
          print(item.getChildren(), prefix + "    ");
        }
      } else {
        System.out.printf("%s├── %s\n", prefix, item.getPresent());
        if (!item.getChildren().isEmpty()) {
          print(item.getChildren(), prefix + "│   ");
        }
      }
    }

  }
}
