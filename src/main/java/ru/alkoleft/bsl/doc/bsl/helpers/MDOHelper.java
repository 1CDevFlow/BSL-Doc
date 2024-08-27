package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.mdo.CommonModule;
import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.mdo.children.ObjectModule;
import com.github._1c_syntax.bsl.types.MdoReference;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.BslContext;

@UtilityClass
public class MDOHelper {

  public MD getOwner(Module module) {
    if (module instanceof ObjectModule) {
      var reference = ((ObjectModule) module).getOwner();
      return BslContext.getCurrent().getConfiguration().findChild(reference).orElse(null);
    } else if (module instanceof CommonModule) {
      return ((CommonModule) module);
    } else {
      return null;
    }
  }

  public String getPresent(MD object) {
    if (object.getSynonym().getContent().isEmpty()) {
      return object.getName();
    }
    var content = object.getSynonym().getContent();
    var itr = content.values().iterator();
    return itr.next();
  }
}
