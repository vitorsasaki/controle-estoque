package com.nexdom.estoque.produto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nexdom.estoque.movimento.MovimentoEstoqueService;
import com.nexdom.estoque.movimento.MovimentoRequest;
import com.nexdom.estoque.movimento.TipoMovimentacao;
import com.nexdom.estoque.shared.ConflitoNegocioException;
import com.nexdom.estoque.support.IntegracaoTestBase;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProdutoIntegracaoTest extends IntegracaoTestBase {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private MovimentoEstoqueService movimentoEstoqueService;

	@Test
	void atualizacaoNaoAlteraSaldo() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-PROD-001",
				"Mouse",
				TipoProduto.ELETRONICO,
				new BigDecimal("80.00"),
				12));

		ProdutoResponse atualizado = produtoService.atualizar(
				produto.id(),
				new ProdutoAtualizacaoRequest(
						"INT-PROD-001",
						"Mouse gamer",
						TipoProduto.ELETRONICO,
						new BigDecimal("90.00")));

		assertEquals(12, atualizado.quantidadeEstoque());
		assertEquals("Mouse gamer", atualizado.descricao());
		assertEquals(new BigDecimal("90.00"), atualizado.valorFornecedor());
	}

	@Test
	void exclusaoComMovimentacaoRetornaConflito() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-PROD-002",
				"Impressora",
				TipoProduto.ELETRONICO,
				new BigDecimal("700.00"),
				5));

		movimentoEstoqueService.registrar(new MovimentoRequest(
				produto.id(),
				TipoMovimentacao.ENTRADA,
				2,
				null,
				null));

		ConflitoNegocioException ex = assertThrows(
				ConflitoNegocioException.class,
				() -> produtoService.excluir(produto.id()));

		assertEquals("PRODUTO_COM_MOVIMENTACOES", ex.getCodigo());
		assertEquals(7, produtoService.buscarPorId(produto.id()).quantidadeEstoque());
	}

	@Test
	void exclusaoSemMovimentacaoRemoveProduto() {
		ProdutoResponse produto = produtoService.criar(new ProdutoRequest(
				"INT-PROD-003",
				"Cabo HDMI",
				TipoProduto.ELETRONICO,
				new BigDecimal("30.00"),
				1));

		produtoService.excluir(produto.id());

		assertThrows(com.nexdom.estoque.shared.RecursoNaoEncontradoException.class, () -> {
			produtoService.buscarPorId(produto.id());
		});
	}
}
