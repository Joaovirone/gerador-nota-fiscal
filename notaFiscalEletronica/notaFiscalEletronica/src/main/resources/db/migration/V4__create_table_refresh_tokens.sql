CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    token TEXT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE(token)
);

CREATE INDEX idx_refresh_token_usuario_id ON refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_token_token ON refresh_tokens(token);
