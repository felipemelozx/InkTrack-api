package com.inktrack.core.gateway;

import com.inktrack.core.domain.Book;

import java.util.UUID;

public interface BookGateway {

  Book save(Book book);

  Book findByIdAndUserId(Long id, UUID userId);

  Book update(Book bookUpdated);
}
