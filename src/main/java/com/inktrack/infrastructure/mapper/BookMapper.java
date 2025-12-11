package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
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
    return new BookEntity(
        book.getId(),
        userEntity,
        book.getTitle(),
        book.getAuthor(),
        book.getTotalPages(),
        book.getPagesRead(),
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }

  public Book entityToDomain(BookEntity bookEntity) {
    User user = userMapper.entityToDomain(bookEntity.getUser());
    return new Book(
        bookEntity.getId(),
        user,
        bookEntity.getTitle(),
        bookEntity.getAuthor(),
        bookEntity.getTotalPages(),
        bookEntity.getPagesRead(),
        bookEntity.getCreatedAt(),
        bookEntity.getUpdatedAt()
    );
  }

  public BookModelInput requestDtoToModelInput(BookCreateRequest bookCreateRequest) {
    return new BookModelInput(
        bookCreateRequest.title(),
        bookCreateRequest.author(),
        bookCreateRequest.totalPages(),
        bookCreateRequest.pagesRead()
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
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }

  public Book modelInputToEntityPreSave(BookModelInput modelInput, User currentUser) {
   return new Book(
        currentUser,
        modelInput.title(),
        modelInput.author(),
        modelInput.totalPages(),
        modelInput.pagesRead()
    );
  }
}
