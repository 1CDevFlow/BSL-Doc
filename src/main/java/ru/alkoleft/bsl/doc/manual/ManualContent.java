package ru.alkoleft.bsl.doc.manual;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import ru.alkoleft.bsl.doc.model.ContentModel;
import ru.alkoleft.bsl.doc.model.ContentModelBuilder;
import ru.alkoleft.bsl.doc.model.Page;
import ru.alkoleft.bsl.doc.options.OutputFormat;

import java.nio.file.Path;

public class ManualContent {
  private final Path location;
  private final Path destination;
  private ContentModel localModel;
  private ContentModel destinationModel;

  public ManualContent(Path location, Path destination) {
    this.location = location;
    this.destination = destination;
  }

  public void buildModel(OutputFormat format) {
    if (location == null) {
      return;
    }
    localModel = ContentModelBuilder.build(location, format);
    destinationModel = null;
  }

  public void copy() {
    if (location == null) {
      return;
    }
    copyFolder(location, destination);
    destinationModel = new ContentModel();
    localModel.getPages().stream()
        .map(it -> createDestinationPage(it, location, destination))
        .forEach(destinationModel.getPages()::add);
  }

  public boolean contains(Path path){
    return destinationModel.getPages()
        .stream()
        .map(Page::getPath)
        .anyMatch(it->it.equals(path));
  }

  private Page createDestinationPage(Page localPage, Path local, Path dest) {
    var destPath = dest.resolve(local.relativize(localPage.getPath()));
    return new Page(destPath, localPage.getTitle(), localPage.getType());
  }

  @SneakyThrows
  private void copyFolder(Path src, Path dest) {
    FileUtils.copyDirectory(src.toFile(), dest.toFile());
  }
}
