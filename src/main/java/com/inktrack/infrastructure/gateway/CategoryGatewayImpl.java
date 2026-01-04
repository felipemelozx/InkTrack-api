package com.inktrack.infrastructure.gateway;

import com.inktrack.core.domain.Category;
import com.inktrack.core.gateway.CategoryGateway;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.mapper.CategoryMapper;
import com.inktrack.infrastructure.persistence.CategoryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryGatewayImpl implements CategoryGateway {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  public CategoryGatewayImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public Optional<Category> getById(Long id) {
    Optional<CategoryEntity> optionalCategory = categoryRepository.findById(id);
    return optionalCategory.map(categoryMapper::entityToDomain);
  }
}
