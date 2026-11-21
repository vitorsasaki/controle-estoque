CREATE TABLE produtos (
    id UUID PRIMARY KEY,
    codigo VARCHAR(64) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    valor_fornecedor NUMERIC(19, 2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL,
    criado_em TIMESTAMPTZ NOT NULL,
    atualizado_em TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_produtos_codigo UNIQUE (codigo),
    CONSTRAINT ck_produtos_quantidade_estoque CHECK (quantidade_estoque >= 0),
    CONSTRAINT ck_produtos_valor_fornecedor CHECK (valor_fornecedor >= 0),
    CONSTRAINT ck_produtos_tipo CHECK (tipo IN ('ELETRONICO', 'ELETRODOMESTICO', 'MOVEL'))
);

CREATE TABLE movimentos_estoque (
    id UUID PRIMARY KEY,
    produto_id UUID NOT NULL,
    tipo_movimentacao VARCHAR(16) NOT NULL,
    valor_venda NUMERIC(19, 2),
    data_venda DATE,
    custo_fornecedor_snapshot NUMERIC(19, 2),
    quantidade INTEGER NOT NULL,
    data_movimentacao TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_movimentos_produto FOREIGN KEY (produto_id) REFERENCES produtos (id),
    CONSTRAINT ck_movimentos_quantidade CHECK (quantidade > 0),
    CONSTRAINT ck_movimentos_tipo CHECK (tipo_movimentacao IN ('ENTRADA', 'SAIDA')),
    CONSTRAINT ck_movimentos_valor_venda CHECK (valor_venda IS NULL OR valor_venda >= 0),
    CONSTRAINT ck_movimentos_custo_snapshot CHECK (custo_fornecedor_snapshot IS NULL OR custo_fornecedor_snapshot >= 0),
    CONSTRAINT ck_movimentos_saida_campos CHECK (
        (tipo_movimentacao = 'ENTRADA' AND valor_venda IS NULL AND data_venda IS NULL AND custo_fornecedor_snapshot IS NULL)
        OR
        (tipo_movimentacao = 'SAIDA' AND valor_venda IS NOT NULL AND data_venda IS NOT NULL AND custo_fornecedor_snapshot IS NOT NULL)
    )
);

CREATE INDEX idx_movimentos_produto_id ON movimentos_estoque (produto_id);
CREATE INDEX idx_movimentos_tipo ON movimentos_estoque (tipo_movimentacao);
CREATE INDEX idx_produtos_tipo ON produtos (tipo);
