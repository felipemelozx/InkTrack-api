package com.inktrack.infrastructure.gateway;

import com.inktrack.InkTrackApplication;
import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.exception.BookNotFoundException;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@Transactional
class BookGatewayIntegrationTest {

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BookMapper bookMapper;

  @Autowired
  private UserMapper userMapper;

  private BookGatewayImpl bookGateway;

  private User savedUser;

  @BeforeEach
  void setUp() {
    bookGateway = new BookGatewayImpl(bookRepository, bookMapper);
    bookRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User(
        null,
        "John Doe",
        "john@example.com",
        "hashed_password",
        LocalDateTime.now()
    );

    UserEntity savedEntity = userRepository.save(userMapper.domainToEntity(user));
    savedUser = userMapper.entityToDomain(savedEntity);
  }

  @Test
  void shouldSaveBookSuccessfully() {
    Book book = new Book(
        savedUser,
        "Clean Code",
        "Robert C. Martin",
        450
    );

    Book savedBook = bookGateway.save(book);

    assertNotNull(savedBook.getId());
    assertEquals("Clean Code", savedBook.getTitle());
    assertEquals(savedUser.getId(), savedBook.getUser().getId());
    assertEquals(0, savedBook.getPagesRead());
    assertNotNull(savedBook.getCreatedAt());

    Optional<BookEntity> entityInDb = bookRepository.findById(savedBook.getId());
    assertTrue(entityInDb.isPresent());
    assertEquals("Clean Code", entityInDb.get().getTitle());
  }

  @Test
  void shouldFindBookByIdAndUserId() {
    Book book = new Book(
        savedUser,
        "Domain-Driven Design",
        "Eric Evans",
        560
    );

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

    assertThrows(
        BookNotFoundException.class,
        () -> bookGateway.findByIdAndUserId(
            nonExistentBookId,
            savedUser.getId()
        )
    );
  }

  @Test
  void shouldNotFindBookFromAnotherUser() {
    Book book = new Book(
        savedUser,
        "Refactoring",
        "Martin Fowler",
        420
    );

    Book savedBook = bookGateway.save(book);

    UUID anotherUserId = UUID.randomUUID();

    assertThrows(
        BookNotFoundException.class,
        () -> bookGateway.findByIdAndUserId(
            savedBook.getId(),
            anotherUserId
        )
    );
  }

  @Test
  void shouldUpdateBookSuccessfully() {
    Book book = new Book(
        savedUser,
        "Effective Java",
        "Joshua Bloch",
        380
    );

    Book savedBook = bookGateway.save(book);

    savedBook.updatePagesRead(120);

    Book updatedBook = bookGateway.update(savedBook);

    assertEquals(120, updatedBook.getPagesRead());
    assertNotNull(updatedBook.getUpdatedAt());

    Optional<BookEntity> entityInDb = bookRepository.findById(updatedBook.getId());
    assertTrue(entityInDb.isPresent());
    assertEquals(120, entityInDb.get().getPagesRead());
  }
}
