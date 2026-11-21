import { requisitar } from './clienteHttp'
import type { LucroPorProduto, Pagina, ProdutoPorTipo, TipoProduto } from '../tipos/estoque'

export function consultarProdutosPorTipo(params: {
  tipo?: TipoProduto | ''
  page?: number
  size?: number
} = {}): Promise<Pagina<ProdutoPorTipo>> {
  const query = new URLSearchParams()
  if (params.tipo) query.set('tipo', params.tipo)
  query.set('page', String(params.page ?? 0))
  if (params.size != null) query.set('size', String(params.size))
  return requisitar<Pagina<ProdutoPorTipo>>(`/api/consultas/produtos-por-tipo?${query.toString()}`)
}

export function consultarLucroPorProduto(params: {
  page?: number
  size?: number
} = {}): Promise<Pagina<LucroPorProduto>> {
  const query = new URLSearchParams()
  query.set('page', String(params.page ?? 0))
  if (params.size != null) query.set('size', String(params.size))
  return requisitar<Pagina<LucroPorProduto>>(`/api/consultas/lucro-por-produto?${query.toString()}`)
}
