package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.mdclasses.Configuration;
import com.github._1c_syntax.mdclasses.mdo.AbstractMDObjectBase;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.bsl.ModuleContext;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class StructureStrategy {

  @Getter
  protected Path path;
  protected String extension;

  StructureStrategy(Path path, String extension) {
    this.path = path;
    this.extension = extension;

    createIfNotExists(path);
  }

  public Path getPath(AbstractMDObjectBase object) {
    Path objectPath = path.resolve(getObjectPath(object));
    createIfNotExists(objectPath.getParent());
    return objectPath;

  }

  public Path getPath(MD object) {
    return path.resolve(getFileName(object));
  }

  public String getLink(AbstractMDObjectBase object) {
    return "../" + getObjectPath(object);
  }

  protected abstract Path getObjectPath(AbstractMDObjectBase object);

  protected abstract Path getModulePath(ModuleContext module);

  public String getFileName(MD object) {
    return String.format("%s.%s", object.getName(), extension);
  }

  public String getFileName(Configuration object) {
    return String.format("configuration.%s", extension);
  }

  @SneakyThrows
  protected void createIfNotExists(Path path) {
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }
  }

  public static class Metadata extends StructureStrategy {
    public Metadata(Path path, String extension) {
      super(path, extension);
    }

    @Override
    public Path getObjectPath(AbstractMDObjectBase object) {
      return Path.of(object.getMdoType().getGroupName(), getFileName(object));
    }

    @Override
    public Path getModulePath(ModuleContext module) {
      var object = module.getOwner();
      var modulePath = path
          .resolve(object.getMdoType().getGroupName())
          .resolve(getFileName(object));
      createIfNotExists(modulePath.getParent());
      return modulePath;
    }
  }

  public static class Subsystems extends StructureStrategy {

    boolean withRootSubsystem;

    public Subsystems(Path path, String extension, boolean withRootSubsystem) {
      super(path, extension);
      this.withRootSubsystem = withRootSubsystem;
    }

    @Override
    protected Path getObjectPath(AbstractMDObjectBase object) {
      var basePath = Path.of("");
      AbstractMDObjectBase owner = object;
      while (true) {
        var subsytems = owner.getIncludedSubsystems();
        if (subsytems.isEmpty()) {
          break;
        }
        owner = subsytems.get(0);
        if (!withRootSubsystem && owner.getIncludedSubsystems().isEmpty()) {
          break;
        }
        basePath = Path.of(owner.getName(), basePath.toString());
      }
      basePath = path.resolve(basePath.toString());
      basePath = basePath.resolve(object.getName());
      createIfNotExists(basePath);
      return basePath;
    }

    @Override
    protected Path getModulePath(ModuleContext module) {
      var object = module.getOwner();
      var basePath = getObjectPath(object);

      var modulePath = basePath.resolve(getFileName(object));
      createIfNotExists(modulePath.getParent());
      return modulePath;
    }
  }
}
