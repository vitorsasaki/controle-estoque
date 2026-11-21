package com.nexdom.estoque.movimento;

import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.produto.ProdutoRepository;
import com.nexdom.estoque.shared.RecursoNaoEncontradoException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimentoEstoqueService {

	private final ProdutoRepository produtoRepository;
	private final MovimentoEstoqueRepository movimentoEstoqueRepository;
	private final ProcessadorMovimentoEstoque processadorMovimentoEstoque;

	public MovimentoEstoqueService(
			ProdutoRepository produtoRepository,
			MovimentoEstoqueRepository movimentoEstoqueRepository,
			ProcessadorMovimentoEstoque processadorMovimentoEstoque) {
		this.produtoRepository = produtoRepository;
		this.movimentoEstoqueRepository = movimentoEstoqueRepository;
		this.processadorMovimentoEstoque = processadorMovimentoEstoque;
	}

	@Transactional
	public MovimentoResponse registrar(MovimentoRequest request) {
		Produto produto = produtoRepository
				.findByIdForUpdate(request.produtoId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));

		MovimentoEstoque movimento = switch (request.tipoMovimentacao()) {
			case ENTRADA -> processadorMovimentoEstoque.processarEntrada(produto, request.quantidade());
			case SAIDA -> processadorMovimentoEstoque.processarSaida(
					produto,
					request.quantidade(),
					request.valorVenda(),
					request.dataVenda());
		};

		produtoRepository.save(produto);
		MovimentoEstoque salvo = movimentoEstoqueRepository.save(movimento);
		return MovimentoResponse.de(salvo);
	}

	@Transactional(readOnly = true)
	public Page<MovimentoResponse> listar(UUID produtoId, Pageable pageable) {
		Page<MovimentoEstoque> movimentos = produtoId == null
				? movimentoEstoqueRepository.findAllByOrderByDataMovimentacaoDesc(pageable)
				: movimentoEstoqueRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produtoId, pageable);
		return movimentos.map(MovimentoResponse::de);
	}
}
