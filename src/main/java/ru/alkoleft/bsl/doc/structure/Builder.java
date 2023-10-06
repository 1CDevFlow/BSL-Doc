package ru.alkoleft.bsl.doc.structure;

import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;

import java.util.List;

@UtilityClass
public class Builder {

  public List<Item> build(BslContext context) {
    return new Subsystems(context).buildStructure();
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
