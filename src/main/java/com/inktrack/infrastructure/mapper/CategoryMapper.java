package com.inktrack.infrastructure.mapper;

import com.inktrack.core.domain.Category;
import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.infrastructure.dtos.category.CategoryResponse;
import com.inktrack.infrastructure.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

  public Category entityToDomain(CategoryEntity categoryEntity) {
    return new Category(
        categoryEntity.getId(),
        categoryEntity.getName(),
        categoryEntity.getCreatedAt()
    );
  }

  public CategoryResponse outputToResponse(CategoryOutput categoryOutput) {
    return new CategoryResponse(categoryOutput.id(), categoryOutput.name(), categoryOutput.createdAt());
  }
}
