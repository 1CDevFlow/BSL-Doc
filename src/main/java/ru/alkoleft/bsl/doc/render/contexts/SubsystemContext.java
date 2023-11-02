package ru.alkoleft.bsl.doc.render.contexts;

import com.github._1c_syntax.bsl.mdo.Constant;
import com.github._1c_syntax.bsl.mdo.Subsystem;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.alkoleft.bsl.doc.bsl.BslContext;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.Page;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Getter
public class SubsystemContext extends BaseContext {
  private final int level;
  private final Subsystem subsystem;
  @Setter
  private ContentModel contentModel;

  @Builder
  public SubsystemContext(int index, String name, String present, String description, Path outputPath, int level, Subsystem subsystem) {
    super(index, name, present, description, outputPath);
    this.level = level;
    this.subsystem = subsystem;
  }

  public List<Constant> getConstants() {
    return BslContext.getCurrent().getSubsystemObjects(subsystem)
        .filter(Constant.class::isInstance)
        .map(Constant.class::cast)
        .collect(Collectors.toList());
  }

  public List<Page> getChildrenPages() {
    if (contentModel != null) {
      return contentModel.getChildrenPages(getOutputPath());
    } else {
      return Collections.emptyList();
    }
  }
}
