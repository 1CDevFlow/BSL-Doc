package ru.alkoleft.bsl.doc.render;

import com.github._1c_syntax.bsl.mdo.MD;
import com.github._1c_syntax.bsl.mdo.MDObject;
import com.github._1c_syntax.mdclasses.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class StructureStrategy {

  protected Path path;
  protected String extension;

  StructureStrategy(Path path, String extension) throws IOException {
    this.path = path;
    this.extension = extension;

    createIfNotExists(path);
  }

  public Path getPath(MDObject object) throws IOException {
    Path objectPath = path.resolve(getObjectPath(object));
    createIfNotExists(objectPath.getParent());
    return objectPath;

  }

  public Path getPath(MD object) {
    return path.resolve(getFileName(object));
  }

  public String getLink(MDObject object) {
    return "../" + getObjectPath(object);
  }

  protected abstract String getObjectPath(MDObject object);

  public String getFileName(MD object) {
    return String.format("%s.%s", object.getName(), extension);
  }

  public String getFileName(Configuration object) {
    return String.format("configuration.%s", extension);
  }

  protected void createIfNotExists(Path path) throws IOException {
    if (!Files.exists(path)) {
      Files.createDirectories(path);
    }
  }

  static class Metadata extends StructureStrategy {
    public Metadata(Path path, String extension) throws IOException {
      super(path, extension);
    }

    @Override
    public String getObjectPath(MDObject object) {
      return Path.of(object.getMdoType().getGroupName(), getFileName(object)).toString();
    }
  }
}
