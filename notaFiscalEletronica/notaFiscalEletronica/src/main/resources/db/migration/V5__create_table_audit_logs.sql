CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    nome_usuario VARCHAR(255) NOT NULL,
    acao VARCHAR(255) NOT NULL,
    entidade VARCHAR(255) NOT NULL,
    entidade_id UUID,
    descricao TEXT,
    dados_antes TEXT,
    dados_depois TEXT,
    ip_origem VARCHAR(45) NOT NULL,
    user_agent TEXT,
    data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    erro TEXT
);

CREATE INDEX idx_audit_log_usuario_id ON audit_logs(usuario_id);
CREATE INDEX idx_audit_log_entidade ON audit_logs(entidade);
CREATE INDEX idx_audit_log_entidade_id ON audit_logs(entidade_id);
CREATE INDEX idx_audit_log_acao ON audit_logs(acao);
CREATE INDEX idx_audit_log_data_hora ON audit_logs(data_hora);
