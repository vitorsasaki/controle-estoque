package com.nexdom.estoque.consulta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nexdom.estoque.movimento.MovimentoEstoqueService;
import com.nexdom.estoque.movimento.MovimentoRequest;
import com.nexdom.estoque.movimento.TipoMovimentacao;
import com.nexdom.estoque.produto.ProdutoRequest;
import com.nexdom.estoque.produto.ProdutoResponse;
import com.nexdom.estoque.produto.ProdutoService;
import com.nexdom.estoque.produto.TipoProduto;
import com.nexdom.estoque.support.IntegracaoTestBase;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class ConsultaIntegracaoTest extends IntegracaoTestBase {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private MovimentoEstoqueService movimentoEstoqueService;

	@Autowired
	private ConsultaEstoqueService consultaEstoqueService;

	@Test
	void consultaPorTipoRetornaSaidasEDisponibilidade() {
		ProdutoResponse notebook = produtoService.criar(new ProdutoRequest(
				"INT-TIP-001",
				"Notebook",
				TipoProduto.ELETRONICO,
				new BigDecimal("2000.00"),
				10));
		produtoService.criar(new ProdutoRequest(
				"INT-TIP-002",
				"Sofá",
				TipoProduto.MOVEL,
				new BigDecimal("1500.00"),
				3));

		movimentoEstoqueService.registrar(new MovimentoRequest(
				notebook.id(),
				TipoMovimentacao.SAIDA,
				4,
				new BigDecimal("2800.00"),
				LocalDate.of(2026, 11, 20)));

		Page<ProdutoPorTipoResponse> resultado =
				consultaEstoqueService.consultarPorTipo(TipoProduto.ELETRONICO, PageRequest.of(0, 20));

		assertEquals(1, resultado.getTotalElements());
		ProdutoPorTipoResponse item = resultado.getContent().getFirst();
		assertEquals(notebook.id(), item.produtoId());
		assertEquals(4, item.quantidadeSaida());
		assertEquals(6, item.quantidadeDisponivel());
	}

	@Test
	void consultaLucroUsaSnapshotHistorico() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-LUC-001",
				"Geladeira",
				TipoProduto.ELETRODOMESTICO,
				new BigDecimal("2000.00"),
				20));

		movimentoEstoqueService.registrar(new MovimentoRequest(
				produto.id(),
				TipoMovimentacao.SAIDA,
				2,
				new BigDecimal("3000.00"),
				LocalDate.of(2026, 1, 15)));
		movimentoEstoqueService.registrar(new MovimentoRequest(
				produto.id(),
				TipoMovimentacao.SAIDA,
				3,
				new BigDecimal("2800.00"),
				LocalDate.of(2026, 2, 15)));

		produtoService.atualizar(
				produto.id(),
				new com.nexdom.estoque.produto.ProdutoAtualizacaoRequest(
						produto.codigo(),
						produto.descricao(),
						produto.tipo(),
						new BigDecimal("2500.00")));

		Page<LucroPorProdutoResponse> lucros =
				consultaEstoqueService.consultarLucroPorProduto(PageRequest.of(0, 50));
		LucroPorProdutoResponse lucro = lucros.getContent().stream()
				.filter(item -> item.produtoId().equals(produto.id()))
				.findFirst()
				.orElseThrow();

		assertEquals(5, lucro.quantidadeTotalSaida());
		assertEquals(new BigDecimal("4400.00"), lucro.lucroTotal());
	}
}
