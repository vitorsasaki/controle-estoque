package com.nexdom.estoque.produto;

import com.nexdom.estoque.movimento.MovimentoEstoqueRepository;
import com.nexdom.estoque.shared.ConflitoNegocioException;
import com.nexdom.estoque.shared.RecursoNaoEncontradoException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final MovimentoEstoqueRepository movimentoEstoqueRepository;

	public ProdutoService(
			ProdutoRepository produtoRepository,
			MovimentoEstoqueRepository movimentoEstoqueRepository) {
		this.produtoRepository = produtoRepository;
		this.movimentoEstoqueRepository = movimentoEstoqueRepository;
	}

	@Transactional
	public ProdutoResponse criar(ProdutoRequest request) {
		if (produtoRepository.existsByCodigo(request.codigo())) {
			throw new ConflitoNegocioException(
					"CODIGO_DUPLICADO",
					"Já existe um produto com o código informado.");
		}
		Produto produto = new Produto(
				request.codigo(),
				request.descricao(),
				request.tipo(),
				request.valorFornecedor(),
				request.quantidadeEstoque());
		return ProdutoResponse.de(produtoRepository.save(produto));
	}

	@Transactional(readOnly = true)
	public Page<ProdutoResponse> listar(TipoProduto tipo, Pageable pageable) {
		Page<Produto> pagina = tipo == null
				? produtoRepository.findAll(pageable)
				: produtoRepository.findByTipo(tipo, pageable);
		return pagina.map(ProdutoResponse::de);
	}

	@Transactional(readOnly = true)
	public ProdutoResponse buscarPorId(UUID id) {
		return ProdutoResponse.de(obterProduto(id));
	}

	@Transactional
	public ProdutoResponse atualizar(UUID id, ProdutoAtualizacaoRequest request) {
		Produto produto = obterProduto(id);
		if (produtoRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
			throw new ConflitoNegocioException(
					"CODIGO_DUPLICADO",
					"Já existe um produto com o código informado.");
		}
		produto.setCodigo(request.codigo());
		produto.setDescricao(request.descricao());
		produto.setTipo(request.tipo());
		produto.setValorFornecedor(request.valorFornecedor());
		return ProdutoResponse.de(produtoRepository.save(produto));
	}

	@Transactional
	public void excluir(UUID id) {
		Produto produto = obterProduto(id);
		if (movimentoEstoqueRepository.existsByProdutoId(id)) {
			throw new ConflitoNegocioException(
					"PRODUTO_COM_MOVIMENTACOES",
					"Não é possível excluir o produto porque existem movimentações vinculadas.");
		}
		produtoRepository.delete(produto);
	}

	private Produto obterProduto(UUID id) {
		return produtoRepository
				.findById(id)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
	}
}
