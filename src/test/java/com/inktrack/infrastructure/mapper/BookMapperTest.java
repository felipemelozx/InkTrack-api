package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.Category;
import com.inktrack.core.domain.User;
import com.inktrack.core.usecases.book.BookModelInput;
import com.inktrack.core.usecases.book.BookModelOutput;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.user.UserOutput;
import com.inktrack.infrastructure.dtos.book.BookCreateRequest;
import com.inktrack.infrastructure.dtos.book.BookResponse;
import com.inktrack.infrastructure.entity.BookEntity;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookMapperTest {

    private BookMapper bookMapper;
    private UserMapper userMapper;
    private CategoryMapper categoryMapper;

    private User testUser;
    private Category testCategory;
    private UserEntity testUserEntity;
    private CategoryEntity testCategoryEntity;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        categoryMapper = new CategoryMapper();
        bookMapper = new BookMapper(userMapper, categoryMapper);

        LocalDateTime userCreatedAt = LocalDateTime.now();
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now().plusHours(1);

        testUser = new User(
            UUID.randomUUID(),
            "John Doe",
            "john@example.com",
            "hashed_password",
            userCreatedAt
        );

        testUserEntity = new UserEntity(
            testUser.getId(),
            testUser.getName(),
            testUser.getEmail(),
            testUser.getPassword()
        );
        testUserEntity.setCreatedAt(userCreatedAt);

        testCategory = new Category(1L, "Fiction", createdAt);
        testCategoryEntity = new CategoryEntity(1L, "Fiction", createdAt);
    }

    @Test
    void domainToEntity_shouldMapCorrectly() {
        Book book = Book.builder()
            .id(1L)
            .user(testUser)
            .category(testCategory)
            .title("Test Book")
            .author("Test Author")
            .totalPages(300)
            .pagesRead(150)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        BookEntity entity = bookMapper.domainToEntity(book);

        assertNotNull(entity);
        assertEquals(book.getId(), entity.getId());
        assertEquals(book.getTitle(), entity.getTitle());
        assertEquals(book.getAuthor(), entity.getAuthor());
        assertEquals(book.getTotalPages(), entity.getTotalPages());
        assertEquals(book.getPagesRead(), entity.getPagesRead());
        assertEquals(book.getProgress(), entity.getProgress());
        assertEquals(book.getCreatedAt(), entity.getCreatedAt());
        assertEquals(book.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(testUser.getId(), entity.getUser().getId());
        assertEquals(testCategory.id(), entity.getCategory().getId());
    }

    @Test
    void entityToDomain_shouldMapCorrectly() {
        BookEntity entity = BookEntity.builder()
            .id(1L)
            .user(testUserEntity)
            .category(testCategoryEntity)
            .title("Test Book")
            .author("Test Author")
            .totalPages(300)
            .pagesRead(150)
            .progress(50)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        Book book = bookMapper.entityToDomain(entity);

        assertNotNull(book);
        assertEquals(entity.getId(), book.getId());
        assertEquals(entity.getTitle(), book.getTitle());
        assertEquals(entity.getAuthor(), book.getAuthor());
        assertEquals(entity.getTotalPages(), book.getTotalPages());
        assertEquals(entity.getPagesRead(), book.getPagesRead());
        assertEquals(entity.getCreatedAt(), book.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), book.getUpdatedAt());
        assertEquals(testUserEntity.getId(), book.getUser().getId());
        assertEquals(testCategoryEntity.getId(), book.getCategory().id());
    }

    @Test
    void requestDtoToModelInput_shouldMapCorrectly() {
        BookCreateRequest request = new BookCreateRequest(
            "Test Book",
            "Test Author",
            300,
            1L,
            null
        );

        BookModelInput modelInput = bookMapper.requestDtoToModelInput(request);

        assertNotNull(modelInput);
        assertEquals(request.title(), modelInput.title());
        assertEquals(request.author(), modelInput.author());
        assertEquals(request.totalPages(), modelInput.totalPages());
        assertEquals(request.categoryId(), modelInput.categoryId());
        assertEquals(request.googleBookId(), modelInput.googleBookId());
    }

    @Test
    void domainToResponse_shouldMapCorrectly() {
        Book book = Book.builder()
            .id(1L)
            .user(testUser)
            .category(testCategory)
            .title("Test Book")
            .author("Test Author")
            .totalPages(300)
            .pagesRead(150)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        BookResponse response = bookMapper.domainToResponse(book);

        assertNotNull(response);
        assertEquals(book.getId(), response.id());
        assertEquals(book.getTitle(), response.title());
        assertEquals(book.getAuthor(), response.author());
        assertEquals(book.getTotalPages(), response.totalPages());
        assertEquals(book.getPagesRead(), response.pagesRead());
        assertEquals(book.getProgress(), response.progress());
        assertEquals(book.getCreatedAt(), response.createdAt());
        assertEquals(book.getUpdatedAt(), response.updatedAt());
        assertEquals(testUser.getId(), response.user().id());
        assertEquals(testCategory.id(), response.category().id());
    }

    @Test
    void modelInputToDomain_shouldMapCorrectly() {
        BookModelInput modelInput = new BookModelInput(
            "Test Book",
            "Test Author",
            300,
            1L,
            null
        );

        Book book = bookMapper.modelInputToDomain(modelInput, testUser, testCategory);

        assertNotNull(book);
        assertEquals(modelInput.title(), book.getTitle());
        assertEquals(modelInput.author(), book.getAuthor());
        assertEquals(modelInput.totalPages(), book.getTotalPages());
        assertEquals(testUser, book.getUser());
        assertEquals(testCategory, book.getCategory());
    }

    @Test
    void modelOutPutToResponse_shouldMapCorrectly() {
        UserOutput userOutput = new UserOutput(
            testUser.getId(),
            testUser.getName(),
            testUser.getEmail(),
            testUser.getCreatedAt()
        );

        CategoryOutput categoryOutput = new CategoryOutput(
            testCategory.id(),
            testCategory.name(),
            testCategory.createdAt()
        );

        BookModelOutput modelOutput = new BookModelOutput(
            1L,
            userOutput,
            categoryOutput,
            "Test Book",
            "Test Author",
            300,
            150,
            50,
            null,
            null,
            createdAt,
            updatedAt
        );

        BookResponse response = bookMapper.modelOutPutToResponse(modelOutput);

        assertNotNull(response);
        assertEquals(modelOutput.id(), response.id());
        assertEquals(modelOutput.title(), response.title());
        assertEquals(modelOutput.author(), response.author());
        assertEquals(modelOutput.totalPages(), response.totalPages());
        assertEquals(modelOutput.pagesRead(), response.pagesRead());
        assertEquals(modelOutput.progress(), response.progress());
        assertEquals(modelOutput.createdAt(), response.createdAt());
        assertEquals(modelOutput.updatedAt(), response.updatedAt());
        assertEquals(userOutput.id(), response.user().id());
        assertEquals(categoryOutput.id(), response.category().id());
    }
}
