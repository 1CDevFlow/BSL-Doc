package ru.alkoleft.bsl.doc.structure;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.options.RenderOptions;

import java.util.List;

public interface StructureBuilder {

  List<Item> build(BslContext context);

  @UtilityClass
  class Factory {
    public StructureBuilder builder(RenderOptions options) {
      if (options.isSubsystemHierarchy()) {
        return new SubsystemsStructureBuilder();
      } else {
        return new FlatStructureBuilder();
      }
    }

    public List<Item> build(BslContext context, RenderOptions options) {
      return builder(options).build(context);
    }

    public void print(List<Item> structure) {
      print(structure, "");
    }

    private void print(List<Item> structure, String prefix) {
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
}
