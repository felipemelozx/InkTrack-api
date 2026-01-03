package com.inktrack.core.usecases.note;

import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.NoteGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteNoteUseCaseTest {

  @Mock
  private NoteGateway noteGateway;

  private DeleteNoteUseCase deleteNoteUseCase;

  @BeforeEach
  void setUp() {
    deleteNoteUseCase = new DeleteNoteUseCaseImpl(noteGateway);
  }

  @Test
  @DisplayName("Should delete note successfully when it exists and belongs to the user")
  void shouldDeleteNoteSuccessfully() {
    Long noteId = 1L;
    Long bookId = 10L;
    UUID userId = UUID.randomUUID();

    when(noteGateway.deleteNote(bookId, noteId, userId)).thenReturn(1);

    assertDoesNotThrow(() -> deleteNoteUseCase.execute(noteId, bookId, userId));

    verify(noteGateway).deleteNote(bookId, noteId, userId);
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when note does not exist or does not belong to user")
  void shouldThrowExceptionWhenNoteNotFound() {
    Long noteId = 1L;
    Long bookId = 10L;
    UUID userId = UUID.randomUUID();

    when(noteGateway.deleteNote(bookId, noteId, userId)).thenReturn(0);

    assertThrows(ResourceNotFoundException.class, () -> deleteNoteUseCase.execute(noteId, bookId, userId));

    verify(noteGateway).deleteNote(bookId, noteId, userId);
  }
}
