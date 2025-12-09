CREATE TABLE public.tb_books (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES public.tb_user(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  author TEXT NOT NULL,
  total_pages INTEGER NOT NULL CHECK (total_pages > 0),
  pages_read INTEGER NOT NULL DEFAULT 0 CHECK (pages_read >= 0 AND pages_read <= total_pages),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
