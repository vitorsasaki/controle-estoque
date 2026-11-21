package com.nexdom.estoque.shared;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratamentoExcecaoGlobal {

	@ExceptionHandler(NegocioException.class)
	public ResponseEntity<ErroResposta> tratarNegocio(NegocioException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(ErroResposta.de(ex.getCodigo(), ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
		List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
				.map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
				.toList();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErroResposta.de("VALIDACAO", "Dados inválidos.", detalhes));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroResposta> tratarGeral(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ErroResposta.de("ERRO_INTERNO", "Erro inesperado ao processar a requisição."));
	}
}
