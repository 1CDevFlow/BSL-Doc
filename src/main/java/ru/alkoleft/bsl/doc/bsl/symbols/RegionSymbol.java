package ru.alkoleft.bsl.doc.bsl.symbols;

import lombok.Value;

@Value
public class RegionSymbol {
  public static final String PUBLIC_REGION_RU = "ПрограммныйИнтерфейс";
  public static final String PUBLIC_REGION_EN = "Public";
  public static final String INTERNAL_REGION_RU = "СлужебныйПрограммныйИнтерфейс";
  public static final String INTERNAL_REGION_EN = "Internal";
  public static final String PRIVATE_REGION_RU = "СлужебныеПроцедурыИФункции";
  public static final String PRIVATE_REGION_EN = "Private";

  String name;
  RegionSymbol parent;

}
