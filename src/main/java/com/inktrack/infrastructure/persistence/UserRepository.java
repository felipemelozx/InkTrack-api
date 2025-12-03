package com.inktrack.infrastructure.persistence;

import com.inktrack.core.domain.User;
import com.inktrack.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<User> findByEmail(String email);
}
