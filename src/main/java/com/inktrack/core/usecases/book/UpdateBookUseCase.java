package com.inktrack.core.usecases.book;

import java.util.UUID;

public interface UpdateBookUseCase {
  BookModelOutput execute(Long id, BookModelInput modelInput, UUID userId);
}
