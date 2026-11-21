package com.nexdom.estoque.consulta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexdom.estoque.movimento.MovimentoEstoque;
import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.produto.TipoProduto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LucroCalculatorTest {

	private LucroCalculator lucroCalculator;
	private Produto produto;

	@BeforeEach
	void setUp() {
		lucroCalculator = new LucroCalculator();
		produto = new Produto(
				"P001",
				"Geladeira",
				TipoProduto.ELETRODOMESTICO,
				new BigDecimal("2000.00"),
				50);
	}

	@Test
	void calculaLucroComMultiplasSaidasEValoresDistintos() {
		MovimentoEstoque saida1 = MovimentoEstoque.saida(
				produto,
				2,
				new BigDecimal("3000.00"),
				LocalDate.of(2026, 1, 10),
				new BigDecimal("2000.00"));
		MovimentoEstoque saida2 = MovimentoEstoque.saida(
				produto,
				3,
				new BigDecimal("2800.00"),
				LocalDate.of(2026, 2, 10),
				new BigDecimal("2000.00"));

		BigDecimal lucro = lucroCalculator.calcularLucroTotal(List.of(saida1, saida2));
		int quantidade = lucroCalculator.somarQuantidadeSaida(List.of(saida1, saida2));

		assertEquals(new BigDecimal("4400.00"), lucro);
		assertEquals(5, quantidade);
	}

	@Test
	void mudancaPosteriorDoCustoNaoAlteraLucroHistorico() {
		MovimentoEstoque saida = MovimentoEstoque.saida(
				produto,
				2,
				new BigDecimal("3000.00"),
				LocalDate.of(2026, 1, 10),
				new BigDecimal("2000.00"));

		produto.setValorFornecedor(new BigDecimal("2500.00"));

		BigDecimal lucro = lucroCalculator.calcularLucroTotal(List.of(saida));

		assertEquals(new BigDecimal("2000.00"), lucro);
		assertEquals(new BigDecimal("2000.00"), saida.getCustoFornecedorSnapshot());
		assertEquals(new BigDecimal("2500.00"), produto.getValorFornecedor());
	}
}
