package com.nexdom.estoque.shared;

import org.springframework.http.HttpStatus;

public class ConflitoNegocioException extends NegocioException {

	public ConflitoNegocioException(String codigo, String mensagem) {
		super(codigo, mensagem, HttpStatus.CONFLICT);
	}
}
