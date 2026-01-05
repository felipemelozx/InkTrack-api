package com.inktrack.core.gateway;

import com.inktrack.core.domain.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryGateway {

  Optional<Category> getById(Long id);

  List<Category> getAll();
}
