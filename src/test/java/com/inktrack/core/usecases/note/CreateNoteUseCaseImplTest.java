package com.inktrack.core.usecases.note;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.Note;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.FieldDomainValidationException;
import com.inktrack.core.gateway.BookGateway;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateNoteUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  @Mock
  private NoteGateway noteGateway;

  @InjectMocks
  private CreateNoteUseCaseImpl createNoteUseCase;

  private User validUser;
  private Book validBook;
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
  }

  @Test
  @DisplayName("Should create the note when the data is valid")
  void execute_shouldCreateNote_whenDataIsValid() {
    String content = "Some note";
    NoteInput noteInput = new NoteInput(validBook.getId(), content);

    when(bookGateway.findByIdAndUserId(validBook.getId(), validUserId)).thenReturn(validBook);

    when(noteGateway.save(any(Note.class))).thenAnswer(invocation -> {
      Note noteToSave = invocation.getArgument(0);
      return new Note(
          11L,
          validBook,
          noteToSave.getContent(),
          noteToSave.getCreatedAt(),
          noteToSave.getUpdatedAt()
      );
    });

    NoteOutput output = createNoteUseCase.execute(validUserId, noteInput);

    assertNotNull(output);
    assertEquals(11L, output.id());
    assertEquals(validBook.getId(), output.bookId());
    assertEquals(content, output.content());
    assertNotNull(output.createAt());
    assertNotNull(output.updatedAt());

    // Verify
    verify(bookGateway).findByIdAndUserId(validBook.getId(), validUserId);
    verify(noteGateway).save(any(Note.class));
  }

  @Test
  @DisplayName("Should throw exception when content is blank")
  void execute_shouldThrowException_whenContentIsBlank() {
    // Arrange
    NoteInput noteInput = new NoteInput(validBook.getId(), "   ");

    // Act & Assert
    FieldDomainValidationException exception = assertThrows(
        FieldDomainValidationException.class,
        () -> createNoteUseCase.execute(validUserId, noteInput)
    );

    assertEquals("content", exception.getFieldName());
    assertEquals("The content can't be blank.", exception.getMessage());

    verify(noteGateway, never()).save(any(Note.class));
  }

  @Test
  @DisplayName("Should throw exception when content length is greater than 255")
  void execute_shouldThrowException_whenContentIsTooLong() {
    String longContent = "a".repeat(256);
    NoteInput noteInput = new NoteInput(validBook.getId(), longContent);

    FieldDomainValidationException exception = assertThrows(
        FieldDomainValidationException.class,
        () -> createNoteUseCase.execute(validUserId, noteInput)
    );

    assertEquals("content", exception.getFieldName());
    assertEquals("The content can't be greater than 255 characters.", exception.getMessage());

    verify(noteGateway, never()).save(any(Note.class));
  }

  @Test
  @DisplayName("Should throw exception when book id is null")
  void execute_shouldThrowException_whenBookIdIsNull() {
    NoteInput noteInput = new NoteInput(null, "Some content");

    FieldDomainValidationException exception = assertThrows(
        FieldDomainValidationException.class,
        () -> createNoteUseCase.execute(validUserId, noteInput)
    );

    assertEquals("bookId", exception.getFieldName());
    assertEquals("The book id can't be null.", exception.getMessage());

    verify(noteGateway, never()).save(any(Note.class));
  }
}


































