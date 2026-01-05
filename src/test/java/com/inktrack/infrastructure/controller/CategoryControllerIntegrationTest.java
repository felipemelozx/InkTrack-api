package com.inktrack.infrastructure.controller;


import com.inktrack.InkTrackApplication;
import com.inktrack.infrastructure.entity.CategoryEntity;
import com.inktrack.infrastructure.persistence.BookRepository;
import com.inktrack.infrastructure.persistence.CategoryRepository;
import com.inktrack.infrastructure.persistence.NoteRepository;
import com.inktrack.infrastructure.persistence.ReadingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.OffsetDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InkTrackApplication.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CategoryControllerIntegrationTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private NoteRepository noteRepository;

  @Autowired
  private ReadingSessionRepository readingSessionRepository;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .apply(springSecurity())
        .build();

    noteRepository.deleteAll();
    readingSessionRepository.deleteAll();
    bookRepository.deleteAll();
    categoryRepository.deleteAll();

    categoryRepository.save(
        new CategoryEntity(
            null,
            "Tattoo",
            OffsetDateTime.now()
        )
    );

    categoryRepository.save(
        new CategoryEntity(
            null,
            "Piercing",
            OffsetDateTime.now()
        )
    );
  }



  @Test
  @DisplayName("Should return all categories")
  void shouldReturnAllCategories() throws Exception {
    mockMvc.perform(get("/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].name").value("Tattoo"))
        .andExpect(jsonPath("$.data[1].name").value("Piercing"));
  }

  @Test
  @DisplayName("Should return category by ID")
  void shouldReturnCategoryById() throws Exception {
    CategoryEntity category = categoryRepository.findAll().get(0);

    mockMvc.perform(get("/categories/{id}", category.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(category.getId()))
        .andExpect(jsonPath("$.data.name").value(category.getName()));
  }

  @Test
  @DisplayName("Should return 404 when category not found by ID")
  void shouldReturn404WhenCategoryNotFoundById() throws Exception {
    mockMvc.perform(get("/categories/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Category not found"))
        .andExpect(jsonPath("$.errors[0].field").value("id"))
        .andExpect(jsonPath("$.errors[0].message").value("Category not found with id: 999"));
  }

}
