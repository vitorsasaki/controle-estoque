package com.nexdom.estoque.shared;

import org.springframework.http.HttpStatus;

public class EstoqueInsuficienteException extends NegocioException {

	public EstoqueInsuficienteException(int saldoDisponivel, int quantidadeSolicitada) {
		super(
				"ESTOQUE_INSUFICIENTE",
				"Estoque insuficiente para a saída. Saldo disponível: "
						+ saldoDisponivel
						+ ", quantidade solicitada: "
						+ quantidadeSolicitada
						+ ".",
				HttpStatus.CONFLICT);
	}
}
