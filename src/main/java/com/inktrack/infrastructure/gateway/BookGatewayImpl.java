package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Book;
import com.inktrack.core.exception.BookNotFoundException;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.usecases.book.GetBookFilter;
import com.inktrack.core.usecases.book.OrderEnum;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.persistence.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookGatewayImpl implements BookGateway {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;

  public BookGatewayImpl(BookRepository bookRepository, BookMapper bookMapper) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
  }

  @Override
  @Transactional
  public Book save(Book book) {
    BookEntity bookEntity = bookMapper.domainToEntity(book);
    BookEntity savedBookEntity = bookRepository.save(bookEntity);
    return bookMapper.entityToDomain(savedBookEntity);
  }

  @Override
  public Book findByIdAndUserId(Long id, UUID userId) {
    Optional<BookEntity> optionalBook = bookRepository.findByIdAndUserId(id, userId);
    if(optionalBook.isEmpty()) {
      throw new BookNotFoundException("id", "Book not found with this id: " + id + " and user id: " + userId);
    }
    return bookMapper.entityToDomain(optionalBook.get());
  }

  @Override
  @Transactional
  public Book update(Book bookUpdated) {
    return save(bookUpdated);
  }

  @Override
  public List<Book> getUserBooksPage(UUID userId, GetBookFilter filter) {
    Sort sort = Sort.by(
        filter.orderEnum().getDirection() == OrderEnum.Direction.ASC
            ? Sort.Direction.ASC
            : Sort.Direction.DESC,
        filter.orderEnum().getField()
    );

    Pageable pageable = PageRequest.of(
        filter.page(),
        filter.size(),
        sort
    );

    return bookRepository.getUserBookPage(userId, filter.title(), filter.categoryId(), pageable)
        .stream()
        .map(bookMapper::entityToDomain)
        .toList();
  }

  @Override
  public long countUserBooks(UUID userId) {
    return bookRepository.countUserBooks(userId, null, null);
  }

  @Override
  public long countUserBooksWithFilters(UUID userId, String title, Long categoryId) {
    return bookRepository.countUserBooks(userId, title, categoryId);
  }

  @Override
  @Transactional
  public boolean deleteByIdAndUserId(Long bookId, UUID userId) {
    int linesAffected = bookRepository.deleteByIdAndUserId(bookId, userId);
    return linesAffected > 0;
  }
}
