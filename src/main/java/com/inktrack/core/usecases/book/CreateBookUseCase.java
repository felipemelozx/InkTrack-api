package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.User;

public interface CreateBookUseCase {

  BookModelOutPut execute(BookModelInput modelInput, User currentUser);
}
