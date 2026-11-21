package com.nexdom.estoque.consulta;

import com.nexdom.estoque.movimento.MovimentoEstoque;
import com.nexdom.estoque.movimento.TipoMovimentacao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LucroCalculator {

	private static final int ESCALA = 2;

	public BigDecimal calcularLucroSaida(MovimentoEstoque movimento) {
		if (movimento.getTipoMovimentacao() != TipoMovimentacao.SAIDA) {
			return BigDecimal.ZERO.setScale(ESCALA, RoundingMode.HALF_UP);
		}
		BigDecimal margemUnitaria = movimento.getValorVenda()
				.subtract(movimento.getCustoFornecedorSnapshot())
				.setScale(ESCALA, RoundingMode.HALF_UP);
		return margemUnitaria
				.multiply(BigDecimal.valueOf(movimento.getQuantidade()))
				.setScale(ESCALA, RoundingMode.HALF_UP);
	}

	public BigDecimal calcularLucroTotal(List<MovimentoEstoque> movimentos) {
		return movimentos.stream()
				.map(this::calcularLucroSaida)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(ESCALA, RoundingMode.HALF_UP);
	}

	public int somarQuantidadeSaida(List<MovimentoEstoque> movimentos) {
		return movimentos.stream()
				.filter(m -> m.getTipoMovimentacao() == TipoMovimentacao.SAIDA)
				.mapToInt(MovimentoEstoque::getQuantidade)
				.sum();
	}
}
