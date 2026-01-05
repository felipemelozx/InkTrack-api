package com.inktrack.core.usecases.metrics;

public enum EvolutionPeriod {
  DAYS_30("30d"),
  MONTHS_3("3m"),
  MONTHS_6("6m"),
  MONTHS_12("12m");

  private final String code;

  EvolutionPeriod(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  public static EvolutionPeriod fromCode(String code) {
    for (EvolutionPeriod period : EvolutionPeriod.values()) {
      if (period.code.equals(code)) {
        return period;
      }
    }
    return DAYS_30;
  }
}
