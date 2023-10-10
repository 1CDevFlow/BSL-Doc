package ru.alkoleft.bsl.doc.render.processor;

import com.github.jknack.handlebars.internal.Files;
import lombok.SneakyThrows;
import ru.alkoleft.bsl.doc.content.processor.TitleCleaner;
import ru.alkoleft.bsl.doc.manual.ManualContent;
import ru.alkoleft.bsl.doc.options.OutputFormat;
import ru.alkoleft.bsl.doc.render.Render;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class MergeProcessor extends OutputProcessor {
  private static final Pattern replacePattern = Pattern.compile("^.*generated_content.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
  TitleCleaner titleCleaner;

  @Override
  public void init(Render render, OutputFormat format, ManualContent manualContent) {
    super.init(render, format, manualContent);
    titleCleaner = TitleCleaner.Factory.create(format);
  }

  @SneakyThrows
  @Override
  public void save(Path itemPath, String content) {
    if (!manualContent.contains(itemPath)) {
      super.save(itemPath, content);
      return;
    }
    var manualContent = Files.read(itemPath.toFile(), StandardCharsets.UTF_8);
    content = titleCleaner.cleanTitle(content);
    var result = replacePattern.matcher(manualContent).replaceFirst(content);
    super.save(itemPath, result);
  }
}
