CREATE TABLE public.tb_reading_sessions
(
    id           BIGSERIAL PRIMARY KEY,
    book_id      BIGINT      NOT NULL,
    minutes      BIGINT      NOT NULL CHECK (minutes > 0),
    pages_read   INTEGER     NOT NULL CHECK (pages_read > 0),
    session_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_reading_session_book
        FOREIGN KEY (book_id)
            REFERENCES public.tb_books (id)
            ON DELETE CASCADE
);
