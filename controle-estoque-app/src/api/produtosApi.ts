import { requisitar } from './clienteHttp'
import type {
  Pagina,
  Produto,
  ProdutoAtualizacaoRequest,
  ProdutoRequest,
  TipoProduto,
} from '../tipos/estoque'

export function listarProdutos(params: {
  tipo?: TipoProduto | ''
  page?: number
  size?: number
}): Promise<Pagina<Produto>> {
  const query = new URLSearchParams()
  if (params.tipo) query.set('tipo', params.tipo)
  query.set('page', String(params.page ?? 0))
  if (params.size != null) query.set('size', String(params.size))
  return requisitar<Pagina<Produto>>(`/api/produtos?${query.toString()}`)
}

export function buscarProduto(id: string): Promise<Produto> {
  return requisitar<Produto>(`/api/produtos/${id}`)
}

export function criarProduto(dados: ProdutoRequest): Promise<Produto> {
  return requisitar<Produto>('/api/produtos', {
    method: 'POST',
    body: JSON.stringify(dados),
  })
}

export function atualizarProduto(id: string, dados: ProdutoAtualizacaoRequest): Promise<Produto> {
  return requisitar<Produto>(`/api/produtos/${id}`, {
    method: 'PUT',
    body: JSON.stringify(dados),
  })
}

export function excluirProduto(id: string): Promise<void> {
  return requisitar<void>(`/api/produtos/${id}`, { method: 'DELETE' })
}
