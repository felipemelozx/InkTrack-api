package com.inktrack.infrastructure.beans;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.NoteGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.ReadingSessionGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.core.usecases.book.CreateBookUseCaseImpl;
import com.inktrack.core.usecases.book.DeleteBookUseCase;
import com.inktrack.core.usecases.book.DeleteBookUseCaseImpl;
import com.inktrack.core.usecases.book.GetBookByIdUseCase;
import com.inktrack.core.usecases.book.GetBookByIdUseCaseImpl;
import com.inktrack.core.usecases.book.GetBooksUseCase;
import com.inktrack.core.usecases.book.GetBooksUseCaseImpl;
import com.inktrack.core.usecases.book.UpdateBookUseCase;
import com.inktrack.core.usecases.book.UpdateBookUseCaseImpl;
import com.inktrack.core.usecases.category.GetAllCategoryUseCase;
import com.inktrack.core.usecases.category.GetAllCategoryUseCaseImpl;
import com.inktrack.core.usecases.category.GetCategoryByIdUseCase;
import com.inktrack.core.usecases.category.GetCategoryByIdUseCaseImpl;
import com.inktrack.core.usecases.note.CreateNoteUseCase;
import com.inktrack.core.usecases.note.CreateNoteUseCaseImpl;
import com.inktrack.core.usecases.note.DeleteNoteUseCase;
import com.inktrack.core.usecases.note.DeleteNoteUseCaseImpl;
import com.inktrack.core.usecases.note.GetNotePaginatorUseCase;
import com.inktrack.core.usecases.note.GetNotePaginatorUseCaseImpl;
import com.inktrack.core.usecases.note.UpdateNoteUseCase;
import com.inktrack.core.usecases.note.UpdateNoteUseCaseImpl;
import com.inktrack.core.usecases.reading.sessions.CreateReadingSessionUseCase;
import com.inktrack.core.usecases.reading.sessions.CreateReadingSessionUseCaseImpl;
import com.inktrack.core.usecases.reading.sessions.DeleteReadingSessionUseCase;
import com.inktrack.core.usecases.reading.sessions.DeleteReadingSessionUseCaseImpl;
import com.inktrack.core.usecases.reading.sessions.GetReadingSessionByBookIdUseCaseImpl;
import com.inktrack.core.usecases.reading.sessions.UpdateReadingSessionUseCase;
import com.inktrack.core.usecases.reading.sessions.UpdateReadingSessionUseCaseImpl;
import com.inktrack.core.usecases.user.CreateUserUseCase;
import com.inktrack.core.usecases.user.CreateUserUseCaseImpl;
import com.inktrack.core.usecases.user.LoginUseCase;
import com.inktrack.core.usecases.user.LoginUseCaseImpl;
import com.inktrack.core.usecases.user.RefreshTokenUseCase;
import com.inktrack.core.usecases.user.RefreshTokenUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeansConfig {

  @Bean
  public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordGateway passwordGateway) {
    return new CreateUserUseCaseImpl(userGateway, passwordGateway);
  }

  @Bean
  public LoginUseCase loginUseCase(UserGateway userGateway, JwtGateway jwtGateway, PasswordGateway passwordGateway) {
    return new LoginUseCaseImpl(userGateway, jwtGateway, passwordGateway);
  }

  @Bean
  public RefreshTokenUseCase refreshTokenUseCase(JwtGateway jwtGateway) {
    return new RefreshTokenUseCaseImpl(jwtGateway);
  }

  @Bean
  public CreateBookUseCase createBookUseCase(BookGateway bookGateway) {
    return new CreateBookUseCaseImpl(bookGateway);
  }

  @Bean
  public UpdateBookUseCase updateBookUseCase(BookGateway bookGateway) {
    return new UpdateBookUseCaseImpl(bookGateway);
  }

  @Bean
  public GetBooksUseCase getBooksUseCase(BookGateway bookGateway) {
    return new GetBooksUseCaseImpl(bookGateway);
  }

  @Bean
  public GetBookByIdUseCase getBookByIdUseCase(BookGateway bookGateway) {
    return new GetBookByIdUseCaseImpl(bookGateway);
  }

  @Bean
  public DeleteBookUseCase deleteBookUseCase(BookGateway bookGateway) {
    return new DeleteBookUseCaseImpl(bookGateway);
  }

  @Bean
  public CreateReadingSessionUseCase createReadingSessionUseCase(
      ReadingSessionGateway readingSessionGateway,
      BookGateway bookGateway
  ) {
    return new CreateReadingSessionUseCaseImpl(readingSessionGateway, bookGateway);
  }

  @Bean
  public GetReadingSessionByBookIdUseCaseImpl getReadingSessionByBookIdUseCase(ReadingSessionGateway gateway) {
    return new GetReadingSessionByBookIdUseCaseImpl(gateway);
  }

  @Bean
  public UpdateReadingSessionUseCase updateReadingSessionUseCase(
      BookGateway bookGateway,
      ReadingSessionGateway readingSessionGateway
  ) {
    return new UpdateReadingSessionUseCaseImpl(readingSessionGateway, bookGateway);
  }

  @Bean
  public DeleteReadingSessionUseCase readingSessionUseCase(
      ReadingSessionGateway readingSessionGateway,
      BookGateway bookGateway
  ) {
    return new DeleteReadingSessionUseCaseImpl(readingSessionGateway, bookGateway);
  }

  @Bean
  public CreateNoteUseCase createNoteUseCase(NoteGateway noteGateway, BookGateway bookGateway) {
    return new CreateNoteUseCaseImpl(noteGateway, bookGateway);
  }

  @Bean
  public GetNotePaginatorUseCase getNotePaginatorUseCase(NoteGateway noteGateway) {
    return new GetNotePaginatorUseCaseImpl(noteGateway);
  }

  @Bean
  public UpdateNoteUseCase updateNoteUseCase(NoteGateway noteGateway) {
    return new UpdateNoteUseCaseImpl(noteGateway);
  }

  @Bean
  public DeleteNoteUseCase deleteNoteUseCase(NoteGateway noteGateway) {
    return new DeleteNoteUseCaseImpl(noteGateway);
  }

  @Bean
  public GetCategoryByIdUseCase getCategoryByIdUseCase(CategoryGateway categoryGateway) {
    return new GetCategoryByIdUseCaseImpl(categoryGateway);
  }

  @Bean
  public GetAllCategoryUseCase getAllCategoryUseCase(CategoryGateway categoryGateway) {
    return new GetAllCategoryUseCaseImpl(categoryGateway);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
