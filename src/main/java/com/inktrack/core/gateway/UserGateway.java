package com.inktrack.core.gateway;

import com.inktrack.core.domain.User;

import java.util.Optional;

public interface UserGateway {
    Optional<User> findByEmail(String email);
    User save(User user);
    User update(User user);
}