package com.nexdom.estoque.consulta;

import java.math.BigDecimal;
import java.util.UUID;

public record LucroPorProdutoResponse(
		UUID produtoId,
		String codigo,
		String descricao,
		Integer quantidadeTotalSaida,
		BigDecimal lucroTotal) {
}
