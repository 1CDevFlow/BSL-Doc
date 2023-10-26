package ru.alkoleft.bsl.doc.render;

import lombok.Value;

@Value
public class TemplatesDefinition {
  String path;
  String headerTemplate;
  String footerTemplate;
}
