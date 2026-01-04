package com.inktrack.core.gateway;

import com.inktrack.core.domain.Book;
import com.inktrack.core.usecases.book.GetBookFilter;

import java.util.List;
import java.util.UUID;

public interface BookGateway {

  Book save(Book book);

  Book findByIdAndUserId(Long id, UUID userId);

  Book update(Book bookUpdated);

  List<Book> getUserBooksPage(UUID userId, GetBookFilter filter);

  long countUserBooks(UUID userId);

  long countUserBooksWithFilters(UUID userId, String title, Long categoryId);

  boolean deleteByIdAndUserId(Long bookId, UUID userId);
}
