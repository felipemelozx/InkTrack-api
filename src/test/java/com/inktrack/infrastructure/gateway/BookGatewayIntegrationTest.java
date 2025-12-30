package com.inktrack.infrastructure.gateway;

import com.inktrack.InkTrackApplication;
import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.BookNotFoundException;
import com.inktrack.core.usecases.book.GetBookFilter;
import com.inktrack.core.usecases.book.OrderEnum;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
class BookGatewayIntegrationTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReadingSessionRepository readingSessionRepository;

  @Autowired
  private BookMapper bookMapper;

  @Autowired
  private UserMapper userMapper;

  private BookGatewayImpl bookGateway;

  private User savedUser;

  @BeforeEach
  void setUp() {
    bookGateway = new BookGatewayImpl(bookRepository, bookMapper);

    User user = new User(
        null,
        "John Doe",
        "john@example.com",
        "hashed_password",
        LocalDateTime.now()
    );

    UserEntity savedEntity =
        userRepository.save(userMapper.domainToEntity(user));

    savedUser = userMapper.entityToDomain(savedEntity);
  }

  @BeforeEach
  void cleanDatabase() {
    readingSessionRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    userRepository.deleteAllInBatch();
  }

  @Test
  void shouldSaveBookSuccessfully() {
    Book book = Book.builder()
        .user(savedUser)
        .title("Clean Code")
        .author("Robert C. Martin")
        .totalPages(450)
        .build();

    Book savedBook = bookGateway.save(book);

    assertNotNull(savedBook.getId());
    assertEquals("Clean Code", savedBook.getTitle());
    assertEquals(savedUser.getId(), savedBook.getUser().getId());
    assertEquals(0, savedBook.getPagesRead());
    assertNotNull(savedBook.getCreatedAt());

    Optional<BookEntity> entityInDb =
        bookRepository.findById(savedBook.getId());

    assertTrue(entityInDb.isPresent());
    assertEquals("Clean Code", entityInDb.get().getTitle());
  }

  @Test
  void shouldFindBookByIdAndUserId() {
    Book book = Book.builder()
        .user(savedUser)
        .title("Domain-Driven Design")
        .author("Eric Evans")
        .totalPages(560)
        .build();

    Book savedBook = bookGateway.save(book);

    Book foundBook = bookGateway.findByIdAndUserId(
        savedBook.getId(),
        savedUser.getId()
    );

    assertEquals(savedBook.getId(), foundBook.getId());
    assertEquals("Domain-Driven Design", foundBook.getTitle());
    assertEquals(savedUser.getId(), foundBook.getUser().getId());
  }

  @Test
  void shouldThrowExceptionWhenBookNotFound() {
    Long nonExistentBookId = 999L;
    UUID userId = savedUser.getId();

    assertThrows(
        BookNotFoundException.class,
        () -> bookGateway.findByIdAndUserId(nonExistentBookId, userId)
    );
  }

  @Test
  void shouldNotFindBookFromAnotherUser() {
    Book book = Book.builder()
        .user(savedUser)
        .title("Refactoring")
        .author("Martin Fowler")
        .totalPages(420)
        .build();

    Book savedBook = bookGateway.save(book);

    UUID anotherUserId = UUID.randomUUID();
    Long bookId = savedBook.getId();

    assertThrows(
        BookNotFoundException.class,
        () -> bookGateway.findByIdAndUserId(bookId, anotherUserId)
    );
  }

  @Test
  void shouldUpdateBookSuccessfully() {
    Book book = Book.builder()
        .user(savedUser)
        .title("Effective Java")
        .author("Joshua Bloch")
        .totalPages(380)
        .build();

    Book savedBook = bookGateway.save(book);

    savedBook.updatePagesRead(120);

    Book updatedBook = bookGateway.update(savedBook);

    assertEquals(120, updatedBook.getPagesRead());
    assertNotNull(updatedBook.getUpdatedAt());

    Optional<BookEntity> entityInDb =
        bookRepository.findById(updatedBook.getId());

    assertTrue(entityInDb.isPresent());
    assertEquals(120, entityInDb.get().getPagesRead());
  }

  @Test
  void shouldReturnUserBooksPageWithPaginationAndSorting() {
    createAndSaveBook("Algoritmos", 100);
    createAndSaveBook("Clean Architecture", 200);
    createAndSaveBook("Banco de Dados", 150);

    OrderEnum order = OrderEnum.TITLE_ASC;
    GetBookFilter filter = new GetBookFilter(0, 2, "", order);

    List<Book> result = bookGateway.getUserBooksPage(savedUser.getId(), filter);

    assertEquals(2, result.size(), "Deve retornar apenas 2 livros (limite da página)");
    assertEquals("Algoritmos", result.get(0).getTitle(), "Primeiro deve ser 'Algoritmos' (Ordem alfabética)");
    assertEquals("Banco de Dados", result.get(1).getTitle(), "Segundo deve ser 'Banco de Dados'");
  }


  @Test
  void shouldReturnUserBooksPageWithPaginationAndSortingWithSortByTitleDesc() {
    createAndSaveBook("Algoritmos", 100);
    createAndSaveBook("Clean Architecture", 200);
    createAndSaveBook("Banco de Dados", 150);

    OrderEnum order = OrderEnum.TITLE_DESC;
    GetBookFilter filter = new GetBookFilter(0, 2, "", order);

    List<Book> result = bookGateway.getUserBooksPage(savedUser.getId(), filter);

    assertEquals(2, result.size(), "Deve retornar apenas 2 livros (limite da página)");
    assertEquals("Clean Architecture", result.get(0).getTitle(), "Primeiro deve ser 'Algoritmos' (Ordem alfabética)");
    assertEquals("Banco de Dados", result.get(1).getTitle(), "Segundo deve ser 'Banco de Dados'");
  }

  @Test
  void shouldCountUserBooks() {
    createAndSaveBook("Book 1", 100);
    createAndSaveBook("Book 2", 100);
    createAndSaveBook("Book 3", 100);

    User otherUser = userMapper.entityToDomain(
        userRepository.save(new UserEntity(null, "Other", "other@mail.com", "SomePassword132#"))
    );
    Book otherBook = Book.builder()
        .user(otherUser).title("Other Book").author("X").totalPages(10).build();
    bookGateway.save(otherBook);

    long count = bookGateway.countUserBooks(savedUser.getId());

    assertEquals(3, count, "Deve contar apenas os livros do usuário logado");
  }

  private void createAndSaveBook(String title, int pages) {
    Book book = Book.builder()
        .user(savedUser)
        .title(title)
        .author("Test Author")
        .totalPages(pages)
        .build();
    bookGateway.save(book);
  }
}
