package com.inktrack.infrastructure.entity;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_books")
public class BookEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;


  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String author;

  @Column(name = "total_pages", nullable = false)
  private Integer totalPages;

  @Column(name = "pages_read", nullable = false)
  private Integer pagesRead;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = OffsetDateTime.now();
  }

  protected BookEntity() {}

  public BookEntity(
      Long id,
      UserEntity user,
      String title,
      String author,
      Integer totalPages,
      Integer pagesRead,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt
  ) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.author = author;
    this.totalPages = totalPages;
    this.pagesRead = pagesRead;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUserId(UserEntity user) {
    this.user = user;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Integer getPagesRead() {
    return pagesRead;
  }

  public void setPagesRead(Integer pagesRead) {
    this.pagesRead = pagesRead;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
