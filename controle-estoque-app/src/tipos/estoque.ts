export type TipoProduto = 'ELETRONICO' | 'ELETRODOMESTICO' | 'MOVEL'
export type TipoMovimentacao = 'ENTRADA' | 'SAIDA'

export interface Produto {
  id: string
  codigo: string
  descricao: string
  tipo: TipoProduto
  valorFornecedor: number
  quantidadeEstoque: number
  criadoEm: string
  atualizadoEm: string
}

export interface ProdutoRequest {
  codigo: string
  descricao: string
  tipo: TipoProduto
  valorFornecedor: number
  quantidadeEstoque: number
}

export interface ProdutoAtualizacaoRequest {
  codigo: string
  descricao: string
  tipo: TipoProduto
  valorFornecedor: number
}

export interface Pagina<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export interface Movimento {
  id: string
  produtoId: string
  produtoCodigo: string
  tipoMovimentacao: TipoMovimentacao
  valorVenda: number | null
  dataVenda: string | null
  custoFornecedorSnapshot: number | null
  quantidade: number
  dataMovimentacao: string
}

export interface MovimentoRequest {
  produtoId: string
  tipoMovimentacao: TipoMovimentacao
  quantidade: number
  valorVenda?: number | null
  dataVenda?: string | null
}

export interface ProdutoPorTipo {
  produtoId: string
  codigo: string
  descricao: string
  tipo: TipoProduto
  quantidadeSaida: number
  quantidadeDisponivel: number
}

export interface LucroPorProduto {
  produtoId: string
  codigo: string
  descricao: string
  quantidadeTotalSaida: number
  lucroTotal: number
}

export interface ErroResposta {
  codigo: string
  mensagem: string
  timestamp: string
  detalhes: string[]
}

export const TIPOS_PRODUTO: { valor: TipoProduto; rotulo: string }[] = [
  { valor: 'ELETRONICO', rotulo: 'Eletrônico' },
  { valor: 'ELETRODOMESTICO', rotulo: 'Eletrodoméstico' },
  { valor: 'MOVEL', rotulo: 'Móvel' },
]

export function rotuloTipoProduto(tipo: TipoProduto): string {
  return TIPOS_PRODUTO.find((item) => item.valor === tipo)?.rotulo ?? tipo
}
