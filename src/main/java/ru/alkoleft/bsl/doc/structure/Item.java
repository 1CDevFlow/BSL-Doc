package ru.alkoleft.bsl.doc.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public abstract class Item {
  private final Object object;
  private final String name;
  private final List<Item> children = new ArrayList<>();

  public String getPresent() {
    return name;
  }

  public abstract void accept(StructureVisitor visitor, int index);

  public void accentChildren(StructureVisitor visitor) {
    for (int i = 0; i < getChildren().size(); i++) {
      getChildren().get(i).accept(visitor, i);
    }
  }
}
