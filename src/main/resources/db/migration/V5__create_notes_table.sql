CREATE TABLE public.tb_notes
(
    id         BIGSERIAL PRIMARY KEY,
    book_id    BIGINT       NOT NULL REFERENCES public.tb_books (id) ON DELETE CASCADE,
    content    VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
