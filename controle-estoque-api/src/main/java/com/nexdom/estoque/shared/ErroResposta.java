package com.nexdom.estoque.shared;

import java.time.Instant;
import java.util.List;

public record ErroResposta(
		String codigo,
		String mensagem,
		Instant timestamp,
		List<String> detalhes) {

	public static ErroResposta de(String codigo, String mensagem) {
		return new ErroResposta(codigo, mensagem, Instant.now(), List.of());
	}

	public static ErroResposta de(String codigo, String mensagem, List<String> detalhes) {
		return new ErroResposta(codigo, mensagem, Instant.now(), detalhes);
	}
}
