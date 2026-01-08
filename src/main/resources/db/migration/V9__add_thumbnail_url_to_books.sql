-- Add thumbnail_url column to tb_books table
ALTER TABLE tb_books ADD COLUMN thumbnail_url VARCHAR(500);

-- Add comment
COMMENT ON COLUMN tb_books.thumbnail_url IS 'URL of the book cover thumbnail from Google Books';
