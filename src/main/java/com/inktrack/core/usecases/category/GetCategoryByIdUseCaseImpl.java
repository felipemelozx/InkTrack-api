package com.inktrack.core.usecases.category;

import com.inktrack.core.domain.Category;
import com.inktrack.core.exception.ResourceNotFoundException;
import com.inktrack.core.gateway.CategoryGateway;

import java.util.Optional;

public class GetCategoryByIdUseCaseImpl implements GetCategoryByIdUseCase {

  private final CategoryGateway categoryGateway;

  public GetCategoryByIdUseCaseImpl(CategoryGateway categoryGateway) {
    this.categoryGateway = categoryGateway;
  }

  @Override
  public CategoryOutput execute(Long id) {
    Optional<Category> optionalCategory = categoryGateway.getById(id);

    if (optionalCategory.isEmpty()) {
      throw new ResourceNotFoundException("Category", "id", "Category not found with id: " + id);
    }
    Category category = optionalCategory.get();
    return new CategoryOutput(category.id(), category.name(), category.createdAt());
  }
}
