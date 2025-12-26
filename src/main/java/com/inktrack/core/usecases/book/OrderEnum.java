package com.inktrack.core.usecases.book;

public enum OrderEnum {

  RECENT("createdAt", Direction.DESC),
  OLDEST("createdAt", Direction.ASC),

  TITLE_ASC("title", Direction.ASC),
  TITLE_DESC("title", Direction.DESC),

  PROGRESS_ASC("progress", Direction.ASC),
  PROGRESS_DESC("progress", Direction.DESC);

  private final String field;
  private final Direction direction;

  OrderEnum(String field, Direction direction) {
    this.field = field;
    this.direction = direction;
  }

  public String getField() {
    return field;
  }

  public Direction getDirection() {
    return direction;
  }

  public enum Direction {
    ASC, DESC
  }
}

