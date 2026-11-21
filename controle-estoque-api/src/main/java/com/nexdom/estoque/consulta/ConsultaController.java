package com.nexdom.estoque.consulta;

import com.nexdom.estoque.produto.TipoProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

	private final ConsultaEstoqueService consultaEstoqueService;

	public ConsultaController(ConsultaEstoqueService consultaEstoqueService) {
		this.consultaEstoqueService = consultaEstoqueService;
	}

	@GetMapping("/produtos-por-tipo")
	public Page<ProdutoPorTipoResponse> produtosPorTipo(
			@RequestParam(required = false) TipoProduto tipo, Pageable pageable) {
		return consultaEstoqueService.consultarPorTipo(tipo, pageable);
	}

	@GetMapping("/lucro-por-produto")
	public Page<LucroPorProdutoResponse> lucroPorProduto(Pageable pageable) {
		return consultaEstoqueService.consultarLucroPorProduto(pageable);
	}
}
