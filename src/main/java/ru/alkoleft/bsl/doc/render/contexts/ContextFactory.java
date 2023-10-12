package ru.alkoleft.bsl.doc.render.contexts;

import com.github._1c_syntax.bsl.types.ModuleType;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import com.github._1c_syntax.mdclasses.mdo.MDCommonModule;
import com.github._1c_syntax.mdclasses.mdo.MDSubsystem;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;

@UtilityClass
public class ContextFactory {

  public ModuleContext create(ModuleInfo module, int index) {
    return ModuleContext.builder()
        .index(index)
        .name(module.getOwner().getName())
        .present(getPresent(module.getOwner()))
        .isCommonModule(module.getOwner() instanceof MDCommonModule)
        .ownerType(module.getOwner().getMdoType().getNameRu())
        .moduleType(getPresent(module.getModule().getModuleType()))
        .methods(module.getMethods())
        .description(module.getDescription())
        .build();
  }

  public SubsystemContext create(MDSubsystem subsystem, int index, int level) {
    return SubsystemContext.builder()
        .index(index)
        .name(subsystem.getName())
        .present(getPresent(subsystem))
        .description(subsystem.getComment())
        .level(level)
        .build();
  }

  private String getPresent(AbstractMDObjectBase object) {
    if (object.getSynonyms().isEmpty()) {
      return object.getName();
    } else {
      return object.getSynonyms().get(0).getContent();
    }
  }

  public Context createContext(Object obj) {
    return Context.newBuilder(obj)
        .resolver(
            MapValueResolver.INSTANCE,
            JavaBeanValueResolver.INSTANCE,
            MethodValueResolver.INSTANCE,
            FieldValueResolver.INSTANCE
        )
        .build();
  }

  private String getPresent(ModuleType moduleType) {
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
