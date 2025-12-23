package com.inktrack.infrastructure.beans;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.core.usecases.book.CreateBookUseCaseImpl;
import com.inktrack.core.usecases.book.GetBooksUseCase;
import com.inktrack.core.usecases.book.GetBooksUseCaseImpl;
import com.inktrack.core.usecases.book.UpdateBookUseCase;
import com.inktrack.core.usecases.book.UpdateBookUseCaseImpl;
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
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
