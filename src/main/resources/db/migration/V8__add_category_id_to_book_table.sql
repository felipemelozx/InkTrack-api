-- Step 1: Add category_id column without NOT NULL constraint
ALTER TABLE public.tb_books
ADD COLUMN category_id BIGINT REFERENCES public.tb_categories(id) ON DELETE RESTRICT;

-- Step 2: Fill NULL values with default category 'OTHER' (id = 1)
UPDATE public.tb_books
SET category_id = 1
WHERE category_id IS NULL;

-- Step 3: Add NOT NULL constraint
ALTER TABLE public.tb_books
ALTER COLUMN category_id SET NOT NULL;

-- Step 4: Create index for query performance
CREATE INDEX idx_book_category ON tb_books(category_id);
