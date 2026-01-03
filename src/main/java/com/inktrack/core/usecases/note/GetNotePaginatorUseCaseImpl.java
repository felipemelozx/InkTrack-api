package com.inktrack.core.usecases.note;

import com.inktrack.core.domain.Note;
import com.inktrack.core.gateway.NoteGateway;
import com.inktrack.core.utils.PageResult;

import java.util.List;
import java.util.UUID;

public class GetNotePaginatorUseCaseImpl implements GetNotePaginatorUseCase {

  private final NoteGateway noteGateway;

  public GetNotePaginatorUseCaseImpl(NoteGateway noteGateway) {
    this.noteGateway = noteGateway;
  }


  @Override
  public PageResult<NoteOutput> execute(Long bookId, UUID userId, int page) {
    PageResult<Note> pageResult = noteGateway.getNotesByBooidAndUserId(bookId, userId, page);

    List<NoteOutput> outputs = pageResult.data().stream()
        .map(n -> new NoteOutput(
            n.getId(),
            n.getBook().getId(),
            n.getContent(),
            n.getCreatedAt(),
            n.getUpdatedAt()
        ))
        .toList();

    return new PageResult<>(
        pageResult.pageSize(),
        pageResult.totalPages(),
        pageResult.currentPage(),
        outputs
    );
  }
}
