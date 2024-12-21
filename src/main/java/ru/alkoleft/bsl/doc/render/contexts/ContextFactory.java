package ru.alkoleft.bsl.doc.render.contexts;

import com.github._1c_syntax.bsl.mdo.CommonModule;
import com.github._1c_syntax.bsl.mdo.Subsystem;
import com.github._1c_syntax.bsl.types.ModuleType;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import lombok.experimental.UtilityClass;
import ru.alkoleft.bsl.doc.bsl.ModuleInfo;
import ru.alkoleft.bsl.doc.bsl.helpers.MDOHelper;

@UtilityClass
public class ContextFactory {

  public ModuleContext create(ModuleInfo module, int index) {
    return ModuleContext.builder()
        .index(index)
        .name(module.getOwner().getName())
        .present(MDOHelper.getPresent(module.getOwner()))
        .isCommonModule(module.getOwner() instanceof CommonModule)
        .ownerType(module.getOwner().getMdoType().getNameRu())
        .moduleType(MDOHelper.getPresent(module.getModule().getModuleType()))
        .methods(module.getMethods())
        .description(module.getDescription())
        .build();
  }

  public SubsystemContext create(Subsystem subsystem, int index, int level) {
    return SubsystemContext.builder()
        .subsystem(subsystem)
        .index(index)
        .name(subsystem.getName())
        .present(MDOHelper.getPresent(subsystem))
        .description(subsystem.getComment())
        .explanation(subsystem.getExplanation().get("ru")) // Берем русскую локаль. Если ее не будет, то вернет любую
        .level(level)
        .build();
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
