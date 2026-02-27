CREATE TABLE notas_fiscais (
    id UUID PRIMARY KEY,
    nota_fiscal_destino VARCHAR(255) NOT NULL,
    valor_total_nf DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE itens_notas_fiscais (
    id UUID PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    quantidade DECIMAL(19, 2) NOT NULL,
    valor_unitario DECIMAL(19, 2) NOT NULL,
    valor_total DECIMAL(19, 2) NOT NULL,
    nota_id UUID NOT NULL,
    CONSTRAINT fk_nota_fiscal FOREIGN KEY (nota_id) REFERENCES notas_fiscais(id)
);