package com.nexdom.estoque.consulta;

import com.nexdom.estoque.produto.TipoProduto;
import java.util.UUID;

public record ProdutoPorTipoResponse(
		UUID produtoId,
		String codigo,
		String descricao,
		TipoProduto tipo,
		Integer quantidadeSaida,
		Integer quantidadeDisponivel) {
}
