package com.inktrack.infrastructure.controller;

import com.inktrack.core.usecases.category.CategoryOutput;
import com.inktrack.core.usecases.category.GetAllCategoryUseCase;
import com.inktrack.core.usecases.category.GetCategoryByIdUseCase;
import com.inktrack.infrastructure.dtos.category.CategoryResponse;
import com.inktrack.infrastructure.mapper.CategoryMapper;
import com.inktrack.infrastructure.utils.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

  private final GetCategoryByIdUseCase getCategoryByIdUseCase;
  private final GetAllCategoryUseCase getAllCategoryUseCase;
  private final CategoryMapper categoryMapper;

  public CategoryController(
      GetCategoryByIdUseCase getCategoryByIdUseCase,
      GetAllCategoryUseCase getAllCategoryUseCase,
      CategoryMapper categoryMapper
  ) {
    this.getCategoryByIdUseCase = getCategoryByIdUseCase;
    this.getAllCategoryUseCase = getAllCategoryUseCase;
    this.categoryMapper = categoryMapper;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
      @PathVariable Long id
  ) {
    CategoryOutput categoryOutput = getCategoryByIdUseCase.execute(id);
    CategoryResponse categoryResponse = categoryMapper.outputToResponse(categoryOutput);
    ApiResponse<CategoryResponse> apiResponse = ApiResponse.success(categoryResponse);
    return ResponseEntity.ok(apiResponse);
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
    List<CategoryOutput> categoryOutputs = getAllCategoryUseCase.execute();
    List<CategoryResponse> categoryResponseList = categoryOutputs.stream()
        .map(categoryMapper::outputToResponse)
        .toList();
    ApiResponse<List<CategoryResponse>> apiResponse = ApiResponse.success(categoryResponseList);
    return ResponseEntity.ok(apiResponse);
  }
}
