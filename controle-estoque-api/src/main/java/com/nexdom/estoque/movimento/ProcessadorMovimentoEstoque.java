package com.nexdom.estoque.movimento;

import com.nexdom.estoque.produto.Produto;
import com.nexdom.estoque.shared.EstoqueInsuficienteException;
import com.nexdom.estoque.shared.NegocioException;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorMovimentoEstoque {

	public MovimentoEstoque processarEntrada(Produto produto, int quantidade) {
		validarQuantidadePositiva(quantidade);
		produto.definirQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
		return MovimentoEstoque.entrada(produto, quantidade);
	}

	public MovimentoEstoque processarSaida(Produto produto, int quantidade, BigDecimal valorVenda, LocalDate dataVenda) {
		validarQuantidadePositiva(quantidade);
		validarCamposSaida(valorVenda, dataVenda);
		int saldo = produto.getQuantidadeEstoque();
		if (quantidade > saldo) {
			throw new EstoqueInsuficienteException(saldo, quantidade);
		}
		BigDecimal custoSnapshot = produto.getValorFornecedor();
		produto.definirQuantidadeEstoque(saldo - quantidade);
		return MovimentoEstoque.saida(produto, quantidade, valorVenda, dataVenda, custoSnapshot);
	}

	private void validarQuantidadePositiva(int quantidade) {
		if (quantidade <= 0) {
			throw new NegocioException(
					"QUANTIDADE_INVALIDA",
					"A quantidade movimentada deve ser maior que zero.",
					HttpStatus.BAD_REQUEST);
		}
	}

	private void validarCamposSaida(BigDecimal valorVenda, LocalDate dataVenda) {
		if (valorVenda == null || valorVenda.compareTo(BigDecimal.ZERO) < 0) {
			throw new NegocioException(
					"VALOR_VENDA_INVALIDO",
					"O valor de venda é obrigatório e não pode ser negativo na saída.",
					HttpStatus.BAD_REQUEST);
		}
		if (dataVenda == null) {
			throw new NegocioException(
					"DATA_VENDA_INVALIDA",
					"A data de venda é obrigatória na saída.",
					HttpStatus.BAD_REQUEST);
		}
	}
}
