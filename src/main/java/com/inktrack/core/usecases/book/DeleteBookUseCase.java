package com.inktrack.core.usecases.book;

import java.util.UUID;

public interface DeleteBookUseCase {

  void execute(Long bookId, UUID userId);

}
