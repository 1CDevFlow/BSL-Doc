package ru.alkoleft.bsl.doc.render.handlebars.helpers;

import com.github._1c_syntax.bsl.mdo.MD;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import ru.alkoleft.bsl.doc.bsl.helpers.MDOHelper;
import ru.alkoleft.bsl.doc.render.contexts.SubsystemContext;

import java.io.IOException;

public class MdoPresent implements Helper<Object> {
  @Override
  public Object apply(Object context, Options options) throws IOException {
    if (context instanceof MD) {
      return MDOHelper.getPresent((MD) context);
    } else if (context instanceof SubsystemContext) {
      return MDOHelper.getPresent(((SubsystemContext) context).getSubsystem());
    } else {
      return context.toString();
    }
  }
}
