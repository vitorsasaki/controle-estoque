package com.nexdom.estoque.produto;

import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProdutoResponse criar(@Valid @RequestBody ProdutoRequest request) {
		return produtoService.criar(request);
	}

	@GetMapping
	public Page<ProdutoResponse> listar(
			@RequestParam(required = false) TipoProduto tipo, Pageable pageable) {
		return produtoService.listar(tipo, pageable);
	}

	@GetMapping("/{id}")
	public ProdutoResponse buscar(@PathVariable UUID id) {
		return produtoService.buscarPorId(id);
	}

	@PutMapping("/{id}")
	public ProdutoResponse atualizar(
			@PathVariable UUID id, @Valid @RequestBody ProdutoAtualizacaoRequest request) {
		return produtoService.atualizar(id, request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void excluir(@PathVariable UUID id) {
		produtoService.excluir(id);
	}
}
