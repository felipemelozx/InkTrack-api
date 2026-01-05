package com.inktrack.core.usecases.category;

import com.inktrack.core.domain.Category;
import com.inktrack.core.gateway.CategoryGateway;

import java.util.List;

public class GetAllCategoryUseCaseImpl implements GetAllCategoryUseCase {

  private final CategoryGateway categoryGateway;

  public GetAllCategoryUseCaseImpl(CategoryGateway categoryGateway) {
    this.categoryGateway = categoryGateway;
  }

  @Override
  public List<CategoryOutput> execute() {
    List<Category> categoryList = categoryGateway.getAll();
    return categoryList.stream().map(
            category -> new CategoryOutput(category.id(), category.name(), category.createdAt()))
        .toList();
  }
}
