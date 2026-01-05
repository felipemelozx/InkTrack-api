package com.inktrack.core.usecases.note;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.Note;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.NoteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateNoteUseCaseImplTest {

  @Mock
  private NoteGateway noteGateway;

  @InjectMocks
  private UpdateNoteUseCaseImpl updateNoteUseCase;

  private User validUser;
  private Book validBook;
  private Note validNote;
  private UUID validUserId;

  @BeforeEach
  void setUp() {
    validUserId = UUID.randomUUID();

    validUser = new User(
        validUserId,
        "Some name",
        "email@gmail.com",
        "StrongPassword12@",
        LocalDateTime.now()
    );

    validBook = Book.builder()
        .id(1L)
        .user(validUser)
        .category(new Category(1L, "Fiction", OffsetDateTime.now()))
        .title("Some title")
        .author("Some author")
        .totalPages(100)
        .pagesRead(0)
        .createdAt(OffsetDateTime.now())
        .updatedAt(OffsetDateTime.now())
        .build();

    validNote = new Note(
        1L,
        validBook,
        "Old content",
        OffsetDateTime.now().minusDays(1),
        OffsetDateTime.now().minusDays(1)
    );
  }

  @Test
  @DisplayName("Should update the note when data is valid")
  void shouldUpdateNoteWhenDataIsValid() {
    NoteInput input = new NoteInput(
        validBook.getId(),
        "Updated content"
    );

    when(noteGateway.getNoteByIdAndBookIdAndUserId(
        validBook.getId(),
        validNote.getId(),
        validUserId
    )).thenReturn(Optional.of(validNote));

    when(noteGateway.update(any(Note.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    NoteOutput output = updateNoteUseCase.execute(
        validNote.getId(),
        validUserId,
        input
    );

    assertNotNull(output);
    assertEquals(validNote.getId(), output.id());
    assertEquals(validBook.getId(), output.bookId());
    assertEquals("Updated content", output.content());
    assertNotNull(output.updatedAt());

    verify(noteGateway, times(1))
        .getNoteByIdAndBookIdAndUserId(validBook.getId(), validNote.getId(), validUserId);

    verify(noteGateway, times(1)).update(any(Note.class));
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when note does not exist")
  void shouldThrowExceptionWhenNoteNotFound() {
    NoteInput input = new NoteInput(
        validBook.getId(),
        "Updated content"
    );

    when(noteGateway.getNoteByIdAndBookIdAndUserId(
        validBook.getId(),
        validNote.getId(),
        validUserId
    )).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> updateNoteUseCase.execute(validNote.getId(), validUserId, input)
    );

    assertEquals("Note", exception.getResource());
    verify(noteGateway, never()).save(any());
  }
}
