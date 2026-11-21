package com.nexdom.estoque.produto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProdutoRequest(
		@NotBlank @Size(max = 64) String codigo,
		@NotBlank @Size(max = 255) String descricao,
		@NotNull TipoProduto tipo,
		@NotNull @DecimalMin("0.00") BigDecimal valorFornecedor,
		@NotNull @Min(0) Integer quantidadeEstoque) {
}
