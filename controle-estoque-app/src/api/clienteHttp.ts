import type { ErroResposta } from '../tipos/estoque'

const baseUrl = import.meta.env.VITE_API_URL ?? ''

export class ApiErro extends Error {
  readonly status: number
  readonly corpo: ErroResposta | null

  constructor(status: number, corpo: ErroResposta | null, mensagem: string) {
    super(mensagem)
    this.status = status
    this.corpo = corpo
  }
}

async function parseErro(resposta: Response): Promise<ApiErro> {
  let corpo: ErroResposta | null = null
  try {
    corpo = (await resposta.json()) as ErroResposta
  } catch {
    corpo = null
  }
  const mensagem = corpo?.mensagem ?? `Falha na requisição (${resposta.status})`
  return new ApiErro(resposta.status, corpo, mensagem)
}

export async function requisitar<T>(caminho: string, init?: RequestInit): Promise<T> {
  const resposta = await fetch(`${baseUrl}${caminho}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  })

  if (!resposta.ok) {
    throw await parseErro(resposta)
  }

  if (resposta.status === 204) {
    return undefined as T
  }

  return (await resposta.json()) as T
}
