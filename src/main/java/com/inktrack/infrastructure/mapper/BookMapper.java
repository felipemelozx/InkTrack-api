package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.BookModelOutput;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.dtos.category.CategoryResponse;
import com.inktrack.infrastructure.dtos.user.UserResponse;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  private final UserMapper userMapper;
  private final CategoryMapper categoryMapper;

  public BookMapper(UserMapper userMapper, CategoryMapper categoryMapper) {
    this.userMapper = userMapper;
    this.categoryMapper = categoryMapper;
  }

  public BookEntity domainToEntity(Book book) {
    UserEntity userEntity = userMapper.domainToEntity(book.getUser());
    CategoryEntity categoryEntity = new CategoryEntity(
        book.getCategory().id(),
        book.getCategory().name(),
        book.getCategory().createdAt()
    );

    return BookEntity.builder()
        .id(book.getId())
        .user(userEntity)
        .category(categoryEntity)
        .title(book.getTitle())
        .author(book.getAuthor())
        .totalPages(book.getTotalPages())
        .pagesRead(book.getPagesRead())
        .progress(book.getProgress())
        .thumbnailUrl(book.getThumbnailUrl())
        .createdAt(book.getCreatedAt())
        .updatedAt(book.getUpdatedAt())
        .build();
  }

  public Book entityToDomain(BookEntity bookEntity) {
    User user = userMapper.entityToDomain(bookEntity.getUser());
    Category category = categoryMapper.entityToDomain(bookEntity.getCategory());

    return Book.builder()
        .id(bookEntity.getId())
        .user(user)
        .category(category)
        .title(bookEntity.getTitle())
        .author(bookEntity.getAuthor())
        .totalPages(bookEntity.getTotalPages())
        .pagesRead(bookEntity.getPagesRead())
        .thumbnailUrl(bookEntity.getThumbnailUrl())
        .createdAt(bookEntity.getCreatedAt())
        .updatedAt(bookEntity.getUpdatedAt())
        .build();
  }

  public BookModelInput requestDtoToModelInput(BookCreateRequest bookCreateRequest) {
    return new BookModelInput(
        bookCreateRequest.title(),
        bookCreateRequest.author(),
        bookCreateRequest.totalPages(),
        bookCreateRequest.categoryId(),
        bookCreateRequest.googleBookId()
    );
  }

  public BookResponse domainToResponse(Book book) {
    UserResponse userResponse = userMapper.userDomainToResponse(book.getUser());
    CategoryResponse categoryResponse = new CategoryResponse(
        book.getCategory().id(),
        book.getCategory().name(),
        book.getCategory().createdAt()
    );

    return new BookResponse(
        book.getId(),
        userResponse,
        categoryResponse,
        book.getTitle(),
        book.getAuthor(),
        book.getTotalPages(),
        book.getPagesRead(),
        book.getProgress(),
        book.getThumbnailUrl(),
        book.getCreatedAt(),
        book.getUpdatedAt()
    );
  }

  public Book modelInputToDomain(BookModelInput modelInput, User currentUser, Category category) {
    return Book.builder()
        .user(currentUser)
        .category(category)
        .title(modelInput.title())
        .author(modelInput.author())
        .totalPages(modelInput.totalPages())
        .build();
  }

  public BookResponse modelOutPutToResponse(BookModelOutput bookModelOutPut) {
    UserResponse userResponse =
        userMapper.userOutputToResponse(bookModelOutPut.user());
    CategoryResponse categoryResponse = categoryMapper.outputToResponse(bookModelOutPut.category());

    return new BookResponse(
        bookModelOutPut.id(),
        userResponse,
        categoryResponse,
        bookModelOutPut.title(),
        bookModelOutPut.author(),
        bookModelOutPut.totalPages(),
        bookModelOutPut.pagesRead(),
        bookModelOutPut.progress(),
        bookModelOutPut.thumbnailUrl(),
        bookModelOutPut.createdAt(),
        bookModelOutPut.updatedAt()
    );
  }
}
