package com.nexdom.estoque.movimento;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentoResponse(
		UUID id,
		UUID produtoId,
		String produtoCodigo,
		TipoMovimentacao tipoMovimentacao,
		BigDecimal valorVenda,
		LocalDate dataVenda,
		BigDecimal custoFornecedorSnapshot,
		Integer quantidade,
		Instant dataMovimentacao) {

	public static MovimentoResponse de(MovimentoEstoque movimento) {
		return new MovimentoResponse(
				movimento.getId(),
				movimento.getProduto().getId(),
				movimento.getProduto().getCodigo(),
				movimento.getTipoMovimentacao(),
				movimento.getValorVenda(),
				movimento.getDataVenda(),
				movimento.getCustoFornecedorSnapshot(),
				movimento.getQuantidade(),
				movimento.getDataMovimentacao());
	}
}
