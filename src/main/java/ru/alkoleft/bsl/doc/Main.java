package ru.alkoleft.bsl.doc;

import picocli.CommandLine;
import ru.alkoleft.bsl.doc.commands.RenderCommand;

public class Main {

  public static void main(String[] args) {
    new CommandLine(new RenderCommand()).execute(args);
  }
}
