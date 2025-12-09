package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;

public interface CreateBookUseCase {

  Book execute(BookModelInput modelInput, User currentUser);
}
