package com.nexdom.estoque.movimento;

import com.nexdom.estoque.produto.Produto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movimentos_estoque")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MovimentoEstoque {

	@Id
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", nullable = false)
	private Produto produto;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_movimentacao", nullable = false, length = 16)
	private TipoMovimentacao tipoMovimentacao;

	@Column(name = "valor_venda", precision = 19, scale = 2)
	private BigDecimal valorVenda;

	@Column(name = "data_venda")
	private LocalDate dataVenda;

	@Column(name = "custo_fornecedor_snapshot", precision = 19, scale = 2)
	private BigDecimal custoFornecedorSnapshot;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(name = "data_movimentacao", nullable = false)
	private Instant dataMovimentacao;

	private MovimentoEstoque(
			Produto produto,
			TipoMovimentacao tipoMovimentacao,
			Integer quantidade,
			BigDecimal valorVenda,
			LocalDate dataVenda,
			BigDecimal custoFornecedorSnapshot) {
		this.id = UUID.randomUUID();
		this.produto = produto;
		this.tipoMovimentacao = tipoMovimentacao;
		this.quantidade = quantidade;
		this.valorVenda = valorVenda;
		this.dataVenda = dataVenda;
		this.custoFornecedorSnapshot = custoFornecedorSnapshot;
	}

	public static MovimentoEstoque entrada(Produto produto, Integer quantidade) {
		return new MovimentoEstoque(produto, TipoMovimentacao.ENTRADA, quantidade, null, null, null);
	}

	public static MovimentoEstoque saida(
			Produto produto,
			Integer quantidade,
			BigDecimal valorVenda,
			LocalDate dataVenda,
			BigDecimal custoFornecedorSnapshot) {
		return new MovimentoEstoque(
				produto,
				TipoMovimentacao.SAIDA,
				quantidade,
				valorVenda,
				dataVenda,
				custoFornecedorSnapshot);
	}

	@PrePersist
	void prePersist() {
		this.dataMovimentacao = Instant.now();
	}
}
