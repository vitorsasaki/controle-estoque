package com.nexdom.estoque.movimento;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimentos")
public class MovimentoController {

	private final MovimentoEstoqueService movimentoEstoqueService;

	public MovimentoController(MovimentoEstoqueService movimentoEstoqueService) {
		this.movimentoEstoqueService = movimentoEstoqueService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MovimentoResponse registrar(@Valid @RequestBody MovimentoRequest request) {
		return movimentoEstoqueService.registrar(request);
	}

	@GetMapping
	public Page<MovimentoResponse> listar(
			@RequestParam(required = false) UUID produtoId, Pageable pageable) {
		return movimentoEstoqueService.listar(produtoId, pageable);
	}
}
