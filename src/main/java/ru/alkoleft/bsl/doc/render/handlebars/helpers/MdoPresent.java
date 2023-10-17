package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import ru.alkoleft.bsl.doc.render.contexts.SubsystemContext;

import java.io.IOException;

public class MdoPresent implements Helper<Object> {
  @Override
  public Object apply(Object context, Options options) throws IOException {
    if (context instanceof AbstractMDObjectBase) {
      return getPresent((AbstractMDObjectBase) context);
    } else if (context instanceof SubsystemContext) {
      return getPresent(((SubsystemContext) context).getSubsystem());
    } else {
      return context.toString();
    }
  }

  private String getPresent(AbstractMDObjectBase object) {
    if (object.getSynonyms().isEmpty()) {
      return object.getName();
    } else {
      return object.getSynonyms().get(0).getContent();
    }
  }
}
