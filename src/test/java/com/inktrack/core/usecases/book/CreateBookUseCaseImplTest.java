package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.gateway.GoogleBooksGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseImplTest {

  @Mock
  private BookGateway bookGateway;

  @Mock
  private CategoryGateway categoryGateway;

  @Mock
  private GoogleBooksGateway googleBooksGateway;

  private CreateBookUseCaseImpl createBookUseCase;

  private User validUser;

  private Category validCategory;

  @BeforeEach
  void setUp() {
    createBookUseCase = new CreateBookUseCaseImpl(bookGateway, categoryGateway, googleBooksGateway);
    validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    validCategory = new Category(1L, "FICTION", OffsetDateTime.now());
  }

  @Test
  @DisplayName("Should create book successfully when all data is valid")
  void execute_shouldCreateBook_whenDataIsValid() {
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, null);
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));

    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1l)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("Clean Code", response.title());
    assertEquals(now, response.createdAt());
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should throw exception when user is null")
  void execute_shouldThrowException_whenUserIsNull() {
    BookModelInput input = new BookModelInput("Title", "Author", 100, 1L, null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, null)
    );

    assertEquals("User not logged in", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when title is blank")
  void execute_shouldThrowException_whenTitleIsBlank() {
    BookModelInput input = new BookModelInput("", "Author", 100, 1L, null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, validUser)
    );

    assertEquals("The title and author not can be black or null.", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when author is blank")
  void execute_shouldThrowException_whenAuthorIsBlank() {
    BookModelInput input = new BookModelInput("Title", "   ", 100, 1L, null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        createBookUseCase.execute(input, validUser)
    );

    assertEquals("The title and author not can be black or null.", exception.getMessage());
    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should throw exception when total pages is negative")
  void execute_shouldThrowException_whenTotalPagesIsNegative() {
    BookModelInput input =
        new BookModelInput("Clean Code", "Robert C. Martin", -1, 1L, null);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> createBookUseCase.execute(input, validUser)
    );

    assertEquals(
        "The total pages must be greater than zero.",
        exception.getMessage()
    );

    verifyNoInteractions(bookGateway);
  }

  @Test
  @DisplayName("Should fetch and set thumbnail when googleBookId is provided")
  void execute_shouldFetchThumbnail_whenGoogleBookIdIsProvided() {
    String googleBookId = "zyTCAlFPjgYC";
    String thumbnailUrl = "http://books.google.com/books/content?id=zyTCAlFPjgYC&printsec=frontcover&img=1&zoom=1&source=gbs_api";
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, googleBookId);
    OffsetDateTime now = OffsetDateTime.now();

    GoogleBooksVolume volume = new GoogleBooksVolume(
        googleBookId,
        "Clean Code",
        List.of("Robert C. Martin"),
        464,
        thumbnailUrl
    );

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(googleBooksGateway.getVolumeById(googleBookId)).thenReturn(Optional.of(volume));
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(thumbnailUrl, response.thumbnailUrl());
    verify(googleBooksGateway).getVolumeById(googleBookId);
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should not call Google Books API when googleBookId is null")
  void execute_shouldNotCallGoogleBooks_whenGoogleBookIdIsNull() {
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, null);
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(null, response.thumbnailUrl());
    verifyNoInteractions(googleBooksGateway);
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should not call Google Books API when googleBookId is blank")
  void execute_shouldNotCallGoogleBooks_whenGoogleBookIdIsBlank() {
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, "   ");
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(null, response.thumbnailUrl());
    verifyNoInteractions(googleBooksGateway);
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should handle exception when Google Books API fails")
  void execute_shouldHandleException_whenGoogleBooksApiFails() {
    String googleBookId = "invalid-id";
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, googleBookId);
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(googleBooksGateway.getVolumeById(googleBookId)).thenThrow(new RuntimeException("API Error"));
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(null, response.thumbnailUrl());
    verify(googleBooksGateway).getVolumeById(googleBookId);
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should set null thumbnail when Google Books returns empty volume")
  void execute_shouldSetNullThumbnail_whenGoogleBooksReturnsEmpty() {
    String googleBookId = "non-existent-id";
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, googleBookId);
    OffsetDateTime now = OffsetDateTime.now();

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(googleBooksGateway.getVolumeById(googleBookId)).thenReturn(Optional.empty());
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(null, response.thumbnailUrl());
    verify(googleBooksGateway).getVolumeById(googleBookId);
    verify(bookGateway).save(any(Book.class));
  }

  @Test
  @DisplayName("Should set null thumbnail when Google Books volume has no thumbnail")
  void execute_shouldSetNullThumbnail_whenVolumeHasNoThumbnail() {
    String googleBookId = "zyTCAlFPjgYC";
    BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 1L, googleBookId);
    OffsetDateTime now = OffsetDateTime.now();

    GoogleBooksVolume volume = new GoogleBooksVolume(
        googleBookId,
        "Clean Code",
        List.of("Robert C. Martin"),
        464,
        null
    );

    when(categoryGateway.getById(1L)).thenReturn(Optional.of(validCategory));
    when(googleBooksGateway.getVolumeById(googleBookId)).thenReturn(Optional.of(volume));
    when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
      Book b = invocation.getArgument(0);
      return Book.builder()
          .id(1L)
          .user(b.getUser())
          .category(b.getCategory())
          .title(b.getTitle())
          .author(b.getAuthor())
          .totalPages(b.getTotalPages())
          .pagesRead(b.getPagesRead())
          .thumbnailUrl(b.getThumbnailUrl())
          .createdAt(now)
          .updatedAt(now)
          .build();
    });

    BookModelOutput response = createBookUseCase.execute(input, validUser);

    assertNotNull(response);
    assertEquals(null, response.thumbnailUrl());
    verify(googleBooksGateway).getVolumeById(googleBookId);
    verify(bookGateway).save(any(Book.class));
  }
}
