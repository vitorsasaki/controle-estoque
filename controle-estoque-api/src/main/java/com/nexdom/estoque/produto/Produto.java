package com.nexdom.estoque.produto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produtos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Produto {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true, length = 64)
	@Setter
	private String codigo;

	@Column(nullable = false)
	@Setter
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	@Setter
	private TipoProduto tipo;

	@Column(name = "valor_fornecedor", nullable = false, precision = 19, scale = 2)
	@Setter
	private BigDecimal valorFornecedor;

	@Column(name = "quantidade_estoque", nullable = false)
	private Integer quantidadeEstoque;

	@Column(name = "criado_em", nullable = false)
	private Instant criadoEm;

	@Column(name = "atualizado_em", nullable = false)
	private Instant atualizadoEm;

	public Produto(
			String codigo,
			String descricao,
			TipoProduto tipo,
			BigDecimal valorFornecedor,
			Integer quantidadeEstoque) {
		this.id = UUID.randomUUID();
		this.codigo = codigo;
		this.descricao = descricao;
		this.tipo = tipo;
		this.valorFornecedor = valorFornecedor;
		this.quantidadeEstoque = quantidadeEstoque;
	}

	public void definirQuantidadeEstoque(Integer quantidadeEstoque) {
		this.quantidadeEstoque = quantidadeEstoque;
	}

	@PrePersist
	void prePersist() {
		Instant agora = Instant.now();
		this.criadoEm = agora;
		this.atualizadoEm = agora;
	}

	@PreUpdate
	void preUpdate() {
		this.atualizadoEm = Instant.now();
	}
}
