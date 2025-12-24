package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.BookModelOutput;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  private final UserMapper userMapper;

  public BookMapper(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public BookEntity domainToEntity(Book book) {
    UserEntity userEntity = userMapper.domainToEntity(book.getUser());

    return BookEntity.builder()
        .id(book.getId())
        .user(userEntity)
        .title(book.getTitle())
        .author(book.getAuthor())
        .totalPages(book.getTotalPages())
        .pagesRead(book.getPagesRead())
        .progress(book.getProgress())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  public Book entityToDomain(BookEntity bookEntity) {
    User user = userMapper.entityToDomain(bookEntity.getUser());

    return Book.builder()
        .id(bookEntity.getId())
        .user(user)
        .title(bookEntity.getTitle())
        .author(bookEntity.getAuthor())
        .totalPages(bookEntity.getTotalPages())
        .pagesRead(bookEntity.getPagesRead())
        .createdAt(bookEntity.getCreatedAt())
        .updatedAt(bookEntity.getUpdatedAt())
        .build();
  }

  public BookModelInput requestDtoToModelInput(BookCreateRequest bookCreateRequest) {
    return new BookModelInput(
        bookCreateRequest.title(),
        bookCreateRequest.author(),
        bookCreateRequest.totalPages()
    );
  }

  public BookResponse domainToResponse(Book book) {
    UserResponse userResponse = userMapper.userDomainToResponse(book.getUser());

    return new BookResponse(
        book.getId(),
        userResponse,
        book.getTitle(),
        book.getAuthor(),
        book.getTotalPages(),
        book.getPagesRead(),
        book.getProgress(),
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }

  public Book modelInputToDomain(BookModelInput modelInput, User currentUser) {
    return Book.builder()
        .user(currentUser)
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .build();
  }

  public BookResponse modelOutPutToResponse(BookModelOutput bookModelOutPut) {
    UserResponse userResponse =
        userMapper.userOutputToResponse(bookModelOutPut.user());

    return new BookResponse(
        bookModelOutPut.id(),
        userResponse,
        bookModelOutPut.title(),
        bookModelOutPut.author(),
        bookModelOutPut.totalPages(),
        bookModelOutPut.pagesRead(),
        bookModelOutPut.progress(),
        bookModelOutPut.createdAt(),
        bookModelOutPut.updatedAt()
    );
  }
}
