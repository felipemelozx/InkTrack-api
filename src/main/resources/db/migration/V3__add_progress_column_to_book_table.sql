-- 0. Criar a coluna
ALTER TABLE public.tb_books
ADD COLUMN progress INTEGER;

-- 1. Normalizar dados existentes
UPDATE public.tb_books
SET progress = (pages_read * 100) / total_pages
WHERE progress IS NULL
  AND total_pages > 0;

-- 2. Definir valor padrão
ALTER TABLE public.tb_books
ALTER COLUMN progress SET DEFAULT 0;

-- 3. Impedir NULL
ALTER TABLE public.tb_books
ALTER COLUMN progress SET NOT NULL;

-- 4. Restringir intervalo (0 a 100)
ALTER TABLE public.tb_books
ADD CONSTRAINT chk_tb_books_progress_range
CHECK (progress >= 0 AND progress <= 100);
