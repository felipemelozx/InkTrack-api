package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Book;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.mapper.BookMapper;
import com.inktrack.infrastructure.persistence.BookRepository;
import org.springframework.stereotype.Component;

@Component
public class BookGatewayImpl implements BookGateway {

  private final BookRepository bookRepository;
  private final BookMapper bookMapper;


  public BookGatewayImpl(BookRepository bookRepository, BookMapper bookMapper) {
    this.bookRepository = bookRepository;
    this.bookMapper = bookMapper;
  }

  @Override
  public Book save(Book book) {
    BookEntity bookEntity = bookMapper.domainToEntity(book);
    BookEntity savedBookEntity = bookRepository.save(bookEntity);
    return bookMapper.entityToDomain(savedBookEntity);
  }
}
