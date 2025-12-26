package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.User;

public interface CreateBookUseCase {

  BookModelOutput execute(BookModelInput modelInput, User currentUser);
}
