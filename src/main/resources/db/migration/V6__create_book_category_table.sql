CREATE TABLE public.tb_categories
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50)              NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_category_name ON tb_categories (name);