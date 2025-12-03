package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.User;
import com.inktrack.core.gateway.UserGateway;
import com.inktrack.infrastructure.entity.UserEntity;
import com.inktrack.infrastructure.mapper.UserMapper;
import com.inktrack.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserGatewayImp implements UserGateway {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserGatewayImp(UserRepository userRepository, UserMapper userMapper) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public User save(User user) {
    UserEntity userEntity = userRepository.save(userMapper.domainToEntity(user));
    return userMapper.entityToDomain(userEntity);
  }

  @Override
  public User update(User user) {
    return null;
  }
}
