package com.inktrack.core.usecases.note;

import com.inktrack.core.utils.PageResult;

import java.util.UUID;

public interface GetNotePaginatorUseCase {

  PageResult<NoteOutput> execute(Long bookId, UUID userId, int page);

}
