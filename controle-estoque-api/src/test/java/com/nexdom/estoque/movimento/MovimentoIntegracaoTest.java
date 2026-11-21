package com.nexdom.estoque.movimento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nexdom.estoque.produto.ProdutoRequest;
import com.nexdom.estoque.produto.ProdutoResponse;
import com.nexdom.estoque.produto.ProdutoService;
import com.nexdom.estoque.produto.TipoProduto;
import com.nexdom.estoque.shared.EstoqueInsuficienteException;
import com.nexdom.estoque.support.IntegracaoTestBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class MovimentoIntegracaoTest extends IntegracaoTestBase {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private MovimentoEstoqueService movimentoEstoqueService;

	@Autowired
	private MovimentoEstoqueRepository movimentoEstoqueRepository;

	@Test
	void persisteMovimentoEAtualizaSaldoNaMesmaTransacao() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-ENT-001",
				"Monitor",
				TipoProduto.ELETRONICO,
				new BigDecimal("800.00"),
				5));

		MovimentoResponse movimento = movimentoEstoqueService.registrar(new MovimentoRequest(
				produto.id(),
				TipoMovimentacao.ENTRADA,
				3,
				null,
				null));

		ProdutoResponse atualizado = produtoService.buscarPorId(produto.id());
		assertEquals(8, atualizado.quantidadeEstoque());
		assertEquals(TipoMovimentacao.ENTRADA, movimento.tipoMovimentacao());
		assertTrue(movimentoEstoqueRepository.existsByProdutoId(produto.id()));
	}

	@Test
	void naoPersisteMovimentoQuandoSaidaFalhaPorEstoqueInsuficiente() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-SAI-001",
				"Teclado",
				TipoProduto.ELETRONICO,
				new BigDecimal("120.00"),
				4));

		assertThrows(
				EstoqueInsuficienteException.class,
				() -> movimentoEstoqueService.registrar(new MovimentoRequest(
						produto.id(),
						TipoMovimentacao.SAIDA,
						10,
						new BigDecimal("200.00"),
						LocalDate.of(2026, 11, 20))));

		ProdutoResponse atualizado = produtoService.buscarPorId(produto.id());
		assertEquals(4, atualizado.quantidadeEstoque());
		assertEquals(0, movimentoEstoqueRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produto.id()).size());
	}

	@Test
	void saidasConcorrentesNaoGeramSaldoNegativo() throws Exception {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-CONC-001",
				"Cadeira",
				TipoProduto.MOVEL,
				new BigDecimal("450.00"),
				10));

		ExecutorService executor = Executors.newFixedThreadPool(2);
		Callable<Boolean> saida = () -> {
			try {
				movimentoEstoqueService.registrar(new MovimentoRequest(
						produto.id(),
						TipoMovimentacao.SAIDA,
						8,
						new BigDecimal("600.00"),
						LocalDate.of(2026, 11, 20)));
				return true;
			} catch (EstoqueInsuficienteException ex) {
				return false;
			}
		};

		List<Future<Boolean>> futures = executor.invokeAll(List.of(saida, saida));
		executor.shutdown();
		assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

		List<Boolean> resultados = new ArrayList<>();
		for (Future<Boolean> future : futures) {
			resultados.add(future.get());
		}

		long sucessos = resultados.stream().filter(Boolean::booleanValue).count();
		long falhas = resultados.stream().filter(r -> !r).count();
		assertEquals(1, sucessos);
		assertEquals(1, falhas);

		ProdutoResponse atualizado = produtoService.buscarPorId(produto.id());
		assertEquals(2, atualizado.quantidadeEstoque());
		assertTrue(atualizado.quantidadeEstoque() >= 0);
		assertEquals(1, movimentoEstoqueRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produto.id()).size());
	}

	@Test
	void listaMovimentosComPaginacao() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-PAG-001",
				"Mouse",
				TipoProduto.ELETRONICO,
				new BigDecimal("80.00"),
				20));

		for (int i = 0; i < 3; i++) {
			movimentoEstoqueService.registrar(new MovimentoRequest(
					produto.id(),
					TipoMovimentacao.ENTRADA,
					1,
					null,
					null));
		}

		Page<MovimentoResponse> pagina =
				movimentoEstoqueService.listar(produto.id(), PageRequest.of(0, 2));

		assertEquals(3, pagina.getTotalElements());
		assertEquals(2, pagina.getTotalPages());
		assertEquals(2, pagina.getContent().size());
	}
}
