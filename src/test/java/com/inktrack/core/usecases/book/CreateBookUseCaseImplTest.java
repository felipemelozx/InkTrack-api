package com.inktrack.core.usecases.book;

import com.inktrack.core.domain.Book;
import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.BookGateway;
import com.inktrack.infrastructure.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookUseCaseImplTest {

    @Mock
    private BookGateway bookGateway;

    @Mock
    private BookMapper bookMapper;

    private CreateBookUseCaseImpl createBookUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        createBookUseCase = new CreateBookUseCaseImpl(bookGateway, bookMapper);
        validUser = new User(UUID.randomUUID(), "Test User", "test@email.com", "Password123!", LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create book successfully when all data is valid")
    void execute_shouldCreateBook_whenDataIsValid() {
        BookModelInput input = new BookModelInput("Clean Code", "Robert C. Martin", 464, 0);
        Book bookDomain = new Book(validUser, input.title(), input.author(), input.totalPages(), input.pagesRead());
        OffsetDateTime now = OffsetDateTime.now();

        when(bookMapper.modelInputToEntityPreSave(input, validUser)).thenReturn(bookDomain);
        when(bookGateway.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            return new Book(1L, b.getUser(), b.getTitle(), b.getAuthor(), b.getTotalPages(), b.getPagesRead(), now, now);
        });

        Book response = createBookUseCase.execute(input, validUser);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Clean Code", response.getTitle());
        assertEquals(now, response.getCreatedAt());
        verify(bookGateway).save(any(Book.class));
    }

    @Test
    @DisplayName("Should throw exception when user is null")
    void execute_shouldThrowException_whenUserIsNull() {
        BookModelInput input = new BookModelInput("Title", "Author", 100, 0);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            createBookUseCase.execute(input, null)
        );
        
        assertEquals("User not logged in", exception.getMessage());
        verifyNoInteractions(bookGateway);
    }

    @Test
    @DisplayName("Should throw exception when pages read is negative")
    void execute_shouldThrowException_whenPagesReadIsNegative() {
        BookModelInput input = new BookModelInput("Title", "Author", 100, -1);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            createBookUseCase.execute(input, validUser)
        );
        
        assertEquals("The page read can not negative or above the total pages.", exception.getMessage());
        verifyNoInteractions(bookGateway);
    }

    @Test
    @DisplayName("Should throw exception when pages read is greater than total pages")
    void execute_shouldThrowException_whenPagesReadGreaterThanTotal() {
        BookModelInput input = new BookModelInput("Title", "Author", 100, 101);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            createBookUseCase.execute(input, validUser)
        );
        
        assertEquals("The page read can not negative or above the total pages.", exception.getMessage());
        verifyNoInteractions(bookGateway);
    }

    @Test
    @DisplayName("Should throw exception when title is blank")
    void execute_shouldThrowException_whenTitleIsBlank() {
        BookModelInput input = new BookModelInput("", "Author", 100, 0);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            createBookUseCase.execute(input, validUser)
        );
        
        assertEquals("The title and author not can be black or null.", exception.getMessage());
        verifyNoInteractions(bookGateway);
    }

    @Test
    @DisplayName("Should throw exception when author is blank")
    void execute_shouldThrowException_whenAuthorIsBlank() {
        BookModelInput input = new BookModelInput("Title", "   ", 100, 0);
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            createBookUseCase.execute(input, validUser)
        );
        
        assertEquals("The title and author not can be black or null.", exception.getMessage());
        verifyNoInteractions(bookGateway);
    }
}
