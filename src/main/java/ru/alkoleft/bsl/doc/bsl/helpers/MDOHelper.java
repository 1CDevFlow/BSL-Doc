package ru.alkoleft.bsl.doc.bsl.helpers;

import com.github._1c_syntax.bsl.mdo.CommonModule;
import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.Module;
import com.github._1c_syntax.bsl.mdo.children.ObjectModule;
import com.github._1c_syntax.bsl.types.ModuleType;
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

  public String getPresent(ModuleType moduleType) {
    return switch (moduleType) {
      case ManagerModule -> "Модуль менеджера";
      case BotModule, ObjectModule -> "Модуль объекта";
      case HTTPServiceModule -> "Модуль http-сервиса";
      case WEBServiceModule -> "Модуль web-сервиса";
      case CommonModule -> "Модуль";
      case CommandModule -> "Модуль команды";
      case RecordSetModule -> "Модуль набора записей";
      case ValueManagerModule -> "Модуль менеджера значений";
      default -> moduleType.name();
    };
  }
}
