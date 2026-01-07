package com.inktrack.infrastructure.persistence;

import java.time.LocalDate;

public interface ReadingEvolutionProjection {

  LocalDate getDate();

  Integer getTotalPages();
}
