package com.nexdom.estoque.produto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProdutoResponse(
		UUID id,
		String codigo,
		String descricao,
		TipoProduto tipo,
		BigDecimal valorFornecedor,
		Integer quantidadeEstoque,
		Instant criadoEm,
		Instant atualizadoEm) {

	public static ProdutoResponse de(Produto produto) {
		return new ProdutoResponse(
				produto.getId(),
				produto.getCodigo(),
				produto.getDescricao(),
				produto.getTipo(),
				produto.getValorFornecedor(),
				produto.getQuantidadeEstoque(),
				produto.getCriadoEm(),
				produto.getAtualizadoEm());
	}
}
