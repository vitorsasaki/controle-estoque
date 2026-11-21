package com.nexdom.estoque.shared;

import org.springframework.http.HttpStatus;

public class RecursoNaoEncontradoException extends NegocioException {

	public RecursoNaoEncontradoException(String mensagem) {
		super("RECURSO_NAO_ENCONTRADO", mensagem, HttpStatus.NOT_FOUND);
	}
}
