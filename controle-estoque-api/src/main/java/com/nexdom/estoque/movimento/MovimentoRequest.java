package com.nexdom.estoque.movimento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentoRequest(
		@NotNull UUID produtoId,
		@NotNull TipoMovimentacao tipoMovimentacao,
		@NotNull @Min(1) Integer quantidade,
		@DecimalMin("0.00") BigDecimal valorVenda,
		LocalDate dataVenda) {
}
