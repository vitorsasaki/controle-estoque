package com.nexdom.estoque.movimento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.produto.ProdutoRepository;
import com.nexdom.estoque.produto.TipoProduto;
import com.nexdom.estoque.shared.EstoqueInsuficienteException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimentoEstoqueServiceTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private MovimentoEstoqueRepository movimentoEstoqueRepository;

	private MovimentoEstoqueService service;
	private Produto produto;

	@BeforeEach
	void setUp() {
		service = new MovimentoEstoqueService(
				produtoRepository,
				movimentoEstoqueRepository,
				new ProcessadorMovimentoEstoque());
		produto = new Produto(
				"P001",
				"Mesa",
				TipoProduto.MOVEL,
				new BigDecimal("500.00"),
				8);
	}

	@Test
	void registraEntradaComLockPessimista() {
		when(produtoRepository.findByIdForUpdate(produto.getId())).thenReturn(Optional.of(produto));
		when(produtoRepository.save(produto)).thenReturn(produto);
		when(movimentoEstoqueRepository.save(any(MovimentoEstoque.class)))
				.thenAnswer(inv -> inv.getArgument(0));

		MovimentoRequest request = new MovimentoRequest(
				produto.getId(),
				TipoMovimentacao.ENTRADA,
				3,
				null,
				null);

		MovimentoResponse response = service.registrar(request);

		assertEquals(11, produto.getQuantidadeEstoque());
		assertEquals(TipoMovimentacao.ENTRADA, response.tipoMovimentacao());
		verify(produtoRepository).findByIdForUpdate(produto.getId());
	}

	@Test
	void registraSaidaComSnapshotDeCusto() {
		when(produtoRepository.findByIdForUpdate(produto.getId())).thenReturn(Optional.of(produto));
		when(produtoRepository.save(produto)).thenReturn(produto);
		when(movimentoEstoqueRepository.save(any(MovimentoEstoque.class)))
				.thenAnswer(inv -> inv.getArgument(0));

		MovimentoRequest request = new MovimentoRequest(
				produto.getId(),
				TipoMovimentacao.SAIDA,
				2,
				new BigDecimal("700.00"),
				LocalDate.of(2026, 11, 19));

		MovimentoResponse response = service.registrar(request);

		assertEquals(6, produto.getQuantidadeEstoque());
		assertEquals(new BigDecimal("500.00"), response.custoFornecedorSnapshot());
		assertEquals(new BigDecimal("700.00"), response.valorVenda());
	}

	@Test
	void naoPersisteQuandoEstoqueInsuficiente() {
		when(produtoRepository.findByIdForUpdate(produto.getId())).thenReturn(Optional.of(produto));

		MovimentoRequest request = new MovimentoRequest(
				produto.getId(),
				TipoMovimentacao.SAIDA,
				20,
				new BigDecimal("700.00"),
				LocalDate.of(2026, 11, 19));

		assertThrows(EstoqueInsuficienteException.class, () -> service.registrar(request));
		verify(movimentoEstoqueRepository, never()).save(any());
		assertEquals(8, produto.getQuantidadeEstoque());
	}
}
