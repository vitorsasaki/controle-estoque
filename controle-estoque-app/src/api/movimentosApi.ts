import { requisitar } from './clienteHttp'
import type { Movimento, MovimentoRequest, Pagina } from '../tipos/estoque'

export function listarMovimentos(params: {
  produtoId?: string
  page?: number
  size?: number
} = {}): Promise<Pagina<Movimento>> {
  const query = new URLSearchParams()
  if (params.produtoId) query.set('produtoId', params.produtoId)
  query.set('page', String(params.page ?? 0))
  if (params.size != null) query.set('size', String(params.size))
  return requisitar<Pagina<Movimento>>(`/api/movimentos?${query.toString()}`)
}

export function registrarMovimento(dados: MovimentoRequest): Promise<Movimento> {
  return requisitar<Movimento>('/api/movimentos', {
    method: 'POST',
    body: JSON.stringify(dados),
  })
}
