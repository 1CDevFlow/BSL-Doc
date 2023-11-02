package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.mdo.CommonModule;
import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.mdo.children.ObjectModule;
import com.github._1c_syntax.bsl.types.MdoReference;
import com.github._1c_syntax.bsl.types.ModuleType;
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

  public String getPresent(ModuleType moduleType) {
    switch (moduleType) {
      case ManagerModule:
        return "Модуль менеджера";
      case BotModule:
      case ObjectModule:
        return "Модуль объекта";
      case HTTPServiceModule:
        return "Модуль http-сервиса";
      case WEBServiceModule:
        return "Модуль web-сервиса";
      case CommonModule:
        return "Модуль";
      case CommandModule:
        return "Модуль команды";
      case RecordSetModule:
        return "Модуль набора записей";
      case ValueManagerModule:
        return "Модуль менеджера значений";
      default:
        return moduleType.name();
    }
  }
}
