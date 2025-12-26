package com.inktrack.core.usecases.book;

import java.util.UUID;

public interface GetBookByIdUseCase {

  BookModelOutput execute(Long bookId, UUID userId);

}
