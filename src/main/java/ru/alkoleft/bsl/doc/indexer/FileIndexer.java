package ru.alkoleft.bsl.doc.indexer;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@AllArgsConstructor
public class FileIndexer {
  private final String fileExtension;

  @SneakyThrows
  public Stream<Path> pagePaths(Path rootPath) {
    return Files.walk(rootPath)
        .filter(path -> path.toFile().isFile())
        .filter(this::matchFileExtension);

  }

  protected boolean matchFileExtension(Path path) {
    return FilenameUtils.getExtension(path.toString()).equals(fileExtension);
  }
}
