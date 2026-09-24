CREATE EXTENSION IF NOT EXISTS pg_trgm;

ALTER TABLE products ADD COLUMN texto_busca TEXT;

CREATE INDEX products_texto_busca_trgm_idx ON products USING gin (texto_busca gin_trgm_ops);
