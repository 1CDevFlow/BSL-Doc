package ru.alkoleft.bsl.doc.render.output;

import com.github.jknack.handlebars.internal.Files;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.model.Page;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class MergeStrategy extends OutputStrategy {
  private static final Pattern replacePattern = Pattern.compile("^.*generated_content.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

  @SneakyThrows
  @Override
  public Page save(Path itemPath, String content) {
    if (manualContent.isNotContains(itemPath)) {
      return super.save(itemPath, content);
    }
    var manualContent = Files.read(itemPath.toFile(), StandardCharsets.UTF_8);
    content = TitleProcessor.getInstance().cleanTitle(content);
    var result = replacePattern.matcher(manualContent).replaceFirst(content);
    return super.save(itemPath, result);
  }
}
