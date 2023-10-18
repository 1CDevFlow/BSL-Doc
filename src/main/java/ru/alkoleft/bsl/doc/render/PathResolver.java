package ru.alkoleft.bsl.doc.render;

import lombok.AllArgsConstructor;
import ru.alkoleft.bsl.doc.options.OutputFormat;
import com.github._1c_syntax.utils.Lazy;
import java.nio.file.Path;
import java.util.Stack;

@AllArgsConstructor
public class PathResolver {

  private final Path destination;
  private final OutputFormat format;
  private final Stack<String> paths = new Stack<>();
  private final Lazy<Path> currentPath = new Lazy<>(this::computeCurrentPath);

  public void entrance(String path) {
    paths.push(path);
    currentPath.clear();
  }

  public void exit() {
    paths.pop();
    currentPath.clear();
  }

  public Path getCurrentPath() {
    return currentPath.getOrCompute();
  }

  public Path getFilePath(String name) {
    return getCurrentPath().resolve(name + "." + format.getExtension());
  }

  private Path computeCurrentPath() {
    Path result = destination;
    var iterator = paths.elements().asIterator();
    while (iterator.hasNext()) {
      result = result.resolve(iterator.next());
    }
    return result;
  }
}
