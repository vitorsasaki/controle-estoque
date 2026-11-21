package com.nexdom.estoque.consulta;

import com.nexdom.estoque.movimento.MovimentoEstoque;
import com.nexdom.estoque.movimento.MovimentoEstoqueRepository;
import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.produto.ProdutoRepository;
import com.nexdom.estoque.produto.TipoProduto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsultaEstoqueService {

	private final ProdutoRepository produtoRepository;
	private final MovimentoEstoqueRepository movimentoEstoqueRepository;
	private final LucroCalculator lucroCalculator;

	public ConsultaEstoqueService(
			ProdutoRepository produtoRepository,
			MovimentoEstoqueRepository movimentoEstoqueRepository,
			LucroCalculator lucroCalculator) {
		this.produtoRepository = produtoRepository;
		this.movimentoEstoqueRepository = movimentoEstoqueRepository;
		this.lucroCalculator = lucroCalculator;
	}

	@Transactional(readOnly = true)
	public Page<ProdutoPorTipoResponse> consultarPorTipo(TipoProduto tipo, Pageable pageable) {
		Page<Produto> produtos = tipo == null
				? produtoRepository.findAll(pageable)
				: produtoRepository.findByTipo(tipo, pageable);
		return produtos.map(produto -> {
			List<MovimentoEstoque> movimentos =
					movimentoEstoqueRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produto.getId());
			return new ProdutoPorTipoResponse(
					produto.getId(),
					produto.getCodigo(),
					produto.getDescricao(),
					produto.getTipo(),
					lucroCalculator.somarQuantidadeSaida(movimentos),
					produto.getQuantidadeEstoque());
		});
	}

	@Transactional(readOnly = true)
	public Page<LucroPorProdutoResponse> consultarLucroPorProduto(Pageable pageable) {
		return produtoRepository.findAll(pageable).map(produto -> {
			List<MovimentoEstoque> movimentos =
					movimentoEstoqueRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produto.getId());
			return new LucroPorProdutoResponse(
					produto.getId(),
					produto.getCodigo(),
					produto.getDescricao(),
					lucroCalculator.somarQuantidadeSaida(movimentos),
					lucroCalculator.calcularLucroTotal(movimentos));
		});
	}
}
