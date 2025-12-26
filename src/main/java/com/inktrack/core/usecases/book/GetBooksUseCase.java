package com.inktrack.core.usecases.book;


import com.inktrack.core.utils.PageResult;

import java.util.UUID;

public interface GetBooksUseCase {
  PageResult<BookModelOutput> execute(UUID userId, GetBookFilter filter);
}
