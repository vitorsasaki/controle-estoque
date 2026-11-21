package com.nexdom.estoque.movimento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.produto.TipoProduto;
import com.nexdom.estoque.shared.EstoqueInsuficienteException;
import com.nexdom.estoque.shared.NegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProcessadorMovimentoEstoqueTest {

	private ProcessadorMovimentoEstoque processador;
	private Produto produto;

	@BeforeEach
	void setUp() {
		processador = new ProcessadorMovimentoEstoque();
		produto = new Produto(
				"P001",
				"Notebook",
				TipoProduto.ELETRONICO,
				new BigDecimal("1000.00"),
				10);
	}

	@Test
	void entradaIncrementaSaldo() {
		MovimentoEstoque movimento = processador.processarEntrada(produto, 5);

		assertEquals(15, produto.getQuantidadeEstoque());
		assertEquals(TipoMovimentacao.ENTRADA, movimento.getTipoMovimentacao());
		assertEquals(5, movimento.getQuantidade());
		assertNull(movimento.getValorVenda());
		assertNull(movimento.getDataVenda());
		assertNull(movimento.getCustoFornecedorSnapshot());
	}

	@Test
	void saidaDecrementaSaldo() {
		MovimentoEstoque movimento = processador.processarSaida(
				produto,
				4,
				new BigDecimal("1500.00"),
				LocalDate.of(2026, 11, 19));

		assertEquals(6, produto.getQuantidadeEstoque());
		assertEquals(TipoMovimentacao.SAIDA, movimento.getTipoMovimentacao());
		assertEquals(new BigDecimal("1000.00"), movimento.getCustoFornecedorSnapshot());
		assertEquals(new BigDecimal("1500.00"), movimento.getValorVenda());
	}

	@Test
	void saidaAcimaDoSaldoLancaEstoqueInsuficiente() {
		EstoqueInsuficienteException ex = assertThrows(
				EstoqueInsuficienteException.class,
				() -> processador.processarSaida(
						produto,
						11,
						new BigDecimal("1500.00"),
						LocalDate.of(2026, 11, 19)));

		assertEquals(10, produto.getQuantidadeEstoque());
		assertEquals("ESTOQUE_INSUFICIENTE", ex.getCodigo());
	}

	@Test
	void quantidadeZeroOuNegativaERecusada() {
		assertThrows(NegocioException.class, () -> processador.processarEntrada(produto, 0));
		assertThrows(NegocioException.class, () -> processador.processarEntrada(produto, -1));
		assertThrows(
				NegocioException.class,
				() -> processador.processarSaida(
						produto,
						0,
						new BigDecimal("10.00"),
						LocalDate.of(2026, 11, 19)));
	}
}
