package com.inktrack.core.gateway;

import com.inktrack.core.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {

    Optional<User> findByEmail(String email);
    
    Optional<User> findById(UUID id);

    User save(User user);
    
    User update(User user);

    void deleteById(UUID id);
}