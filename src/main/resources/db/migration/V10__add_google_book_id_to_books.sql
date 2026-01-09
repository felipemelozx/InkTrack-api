ALTER TABLE tb_books
    ADD COLUMN google_book_id VARCHAR(50);

CREATE INDEX idx_books_google_book_id ON tb_books (google_book_id);
