package ru.alkoleft.bsl.doc.model;

import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class ContentModel {
  List<Page> pages = new ArrayList<>();
}
