package com.inktrack.infrastructure.beans;

import com.inktrack.core.gateway.JwtGateway;
import com.inktrack.core.gateway.PasswordGateway;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.core.usecases.user.CreateUserUseCase;
import com.inktrack.core.usecases.user.CreateUserUseCaseImp;
import com.inktrack.core.usecases.user.LoginUseCase;
import com.inktrack.core.usecases.user.LoginUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BeansConfig {

  @Bean
  public CreateUserUseCase createUserUseCase(UserGateway userGateway, PasswordGateway passwordGateway) {
    return new CreateUserUseCaseImp(userGateway, passwordGateway);
  }

  @Bean
  public LoginUseCase loginUseCase(UserGateway userGateway, JwtGateway jwtGateway, PasswordGateway passwordGateway) {
    return new LoginUseCaseImpl(userGateway, jwtGateway, passwordGateway);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
