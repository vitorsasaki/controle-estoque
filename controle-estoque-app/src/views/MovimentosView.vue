<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { listarMovimentos } from '../api/movimentosApi'
import { ApiErro } from '../api/clienteHttp'
import type { Movimento } from '../tipos/estoque'

const movimentos = ref<Movimento[]>([])
const carregando = ref(false)
const erro = ref('')
const pagina = ref(0)
const totalPaginas = ref(0)
const totalElementos = ref(0)

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await listarMovimentos({
      page: pagina.value,
    })
    movimentos.value = resposta.content
    totalPaginas.value = resposta.totalPages
    totalElementos.value = resposta.totalElements
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar as movimentações.'
  } finally {
    carregando.value = false
  }
}

function formatarData(valor: string | null) {
  if (!valor) return '—'
  return new Date(valor).toLocaleString('pt-BR')
}

function formatarMoeda(valor: number | null) {
  if (valor === null || valor === undefined) return '—'
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

onMounted(() => {
  void carregar()
})
</script>

<template>
  <section class="painel">
    <div class="cabecalho-secao">
      <h1>Histórico de movimentações</h1>
      <RouterLink class="botao" to="/movimentos/novo">Nova movimentação</RouterLink>
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="carregando" class="estado">Carregando histórico...</div>
    <div v-else-if="movimentos.length === 0" class="estado">Nenhuma movimentação registrada.</div>
    <div v-else class="tabela-wrapper">
      <table>
        <thead>
          <tr>
            <th>Data</th>
            <th>Produto</th>
            <th>Tipo</th>
            <th>Quantidade</th>
            <th>Valor venda</th>
            <th>Custo snapshot</th>
            <th>Data venda</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="movimento in movimentos" :key="movimento.id">
            <td>{{ formatarData(movimento.dataMovimentacao) }}</td>
            <td>{{ movimento.produtoCodigo }}</td>
            <td>{{ movimento.tipoMovimentacao }}</td>
            <td>{{ movimento.quantidade }}</td>
            <td>{{ formatarMoeda(movimento.valorVenda) }}</td>
            <td>{{ formatarMoeda(movimento.custoFornecedorSnapshot) }}</td>
            <td>{{ movimento.dataVenda ?? '—' }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="totalPaginas > 1" class="paginacao">
      <button
        class="botao secundario"
        type="button"
        :disabled="pagina === 0 || carregando"
        @click="pagina--; carregar()"
      >
        Anterior
      </button>
      <span>Página {{ pagina + 1 }} de {{ totalPaginas }} ({{ totalElementos }})</span>
      <button
        class="botao secundario"
        type="button"
        :disabled="pagina + 1 >= totalPaginas || carregando"
        @click="pagina++; carregar()"
      >
        Próxima
      </button>
    </div>
  </section>
</template>
