package com.nexdom.estoque.movimento;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, UUID> {

	boolean existsByProdutoId(UUID produtoId);

	List<MovimentoEstoque> findByProdutoIdOrderByDataMovimentacaoDesc(UUID produtoId);

	Page<MovimentoEstoque> findByProdutoIdOrderByDataMovimentacaoDesc(UUID produtoId, Pageable pageable);

	Page<MovimentoEstoque> findAllByOrderByDataMovimentacaoDesc(Pageable pageable);
}
