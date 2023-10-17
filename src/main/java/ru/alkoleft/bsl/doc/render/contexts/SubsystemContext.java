package ru.alkoleft.bsl.doc.render.contexts;

import com.github._1c_syntax.mdclasses.mdo.MDConstant;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
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
  private final MDSubsystem subsystem;
  @Setter
  private ContentModel contentModel;

  @Builder
  public SubsystemContext(int index, String name, String present, String description, Path outputPath, int level, MDSubsystem subsystem) {
    super(index, name, present, description, outputPath);
    this.level = level;
    this.subsystem = subsystem;
  }

  public List<MDConstant> getConstants() {
    var c = BslContext.getCurrent().getSubsystemObjects(subsystem)
        .filter(MDConstant.class::isInstance)
        .map(MDConstant.class::cast)
        .collect(Collectors.toList());
    return c;
  }

  public List<Page> getChildrenPages() {
    if (contentModel != null) {
      return contentModel.getChildrenPages(getOutputPath());
    } else {
      return Collections.emptyList();
    }
  }
}
