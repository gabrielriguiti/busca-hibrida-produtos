CREATE INDEX products_embedding_hnsw_idx ON products USING hnsw (embedding vector_cosine_ops);
