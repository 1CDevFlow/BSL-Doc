package ru.alkoleft.bsl.doc.render.contexts;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.Page;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
public class SubsystemContext extends BaseContext {
  private final int level;
  @Setter
  ContentModel contentModel;

  @Builder
  public SubsystemContext(Path outputPath, int index, String name, String present, String description, int level) {
    super(outputPath, index, name, present, description);
    this.level = level;
  }

  public List<Page> getChildren() {
    if (contentModel != null) {
      return contentModel.getChildrenPages(getOutputPath());
    } else {
      return Collections.emptyList();
    }
  }
}
