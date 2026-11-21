package com.nexdom.estoque.produto;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {

	boolean existsByCodigo(String codigo);

	boolean existsByCodigoAndIdNot(String codigo, UUID id);

	Page<Produto> findByTipo(TipoProduto tipo, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select p from Produto p where p.id = :id")
	Optional<Produto> findByIdForUpdate(@Param("id") UUID id);
}
