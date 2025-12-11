package com.inktrack.infrastructure.beans;

import com.inktrack.core.gateway.BookGateway;
import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.usecases.book.CreateBookUseCase;
import com.inktrack.core.usecases.book.CreateBookUseCaseImpl;
import com.inktrack.core.usecases.user.CreateUserUseCase;
import com.inktrack.core.usecases.user.CreateUserUseCaseImpl;
import com.inktrack.core.usecases.user.LoginUseCase;
import com.inktrack.core.usecases.user.LoginUseCaseImpl;
import com.inktrack.core.usecases.user.RefreshTokenUseCase;
import com.inktrack.core.usecases.user.RefreshTokenUseCaseImpl;
import com.inktrack.infrastructure.mapper.BookMapper;
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
  public CreateBookUseCase createBookUseCase(BookGateway bookGateway, BookMapper bookMapper) {
    return new CreateBookUseCaseImpl(bookGateway, bookMapper);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
