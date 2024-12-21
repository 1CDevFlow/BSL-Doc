package ru.alkoleft.bsl.doc.render.output;

import com.github.jknack.handlebars.internal.Files;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.content.processor.TitleProcessor;
import ru.alkoleft.bsl.doc.model.Page;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class MergeStrategy extends OutputStrategy {
  private static final Pattern REPLACE_PATTERN =
      Pattern.compile("([\\w\\W]*)(^.*generated_content.*$\\n?)([\\w\\W]*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

  @SneakyThrows
  @Override
  public Page save(Path itemPath, String content) {
    if (manualContent.isNotContains(itemPath)) {
      return super.save(itemPath, content);
    }
    var fileContent = Files.read(itemPath.toFile(), StandardCharsets.UTF_8);
    var parts = REPLACE_PATTERN.matcher(fileContent);
    content = TitleProcessor.getInstance().cleanTitle(content);
    if (parts.find()) {
      var result = parts.group(1) + content + parts.group(3);
      return super.save(itemPath, result);
    } else {
      return super.save(itemPath, content);
    }
  }
}
