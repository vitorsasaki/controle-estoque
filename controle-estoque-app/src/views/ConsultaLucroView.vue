<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { consultarLucroPorProduto } from '../api/consultasApi'
import { ApiErro } from '../api/clienteHttp'
import type { LucroPorProduto } from '../tipos/estoque'

const itens = ref<LucroPorProduto[]>([])
const carregando = ref(false)
const erro = ref('')
const pagina = ref(0)
const totalPaginas = ref(0)
const totalElementos = ref(0)

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await consultarLucroPorProduto({
      page: pagina.value,
    })
    itens.value = resposta.content
    totalPaginas.value = resposta.totalPages
    totalElementos.value = resposta.totalElements
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar o lucro.'
  } finally {
    carregando.value = false
  }
}

function formatarMoeda(valor: number) {
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

onMounted(() => {
  void carregar()
})
</script>

<template>
  <section class="painel">
    <div class="cabecalho-secao">
      <h1>Lucro por produto</h1>
    </div>

    <div class="alerta aviso">
      Lucro bruto simplificado: (valor de venda − custo do fornecedor na saída) × quantidade.
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="carregando" class="estado">Carregando lucros...</div>
    <div v-else-if="itens.length === 0" class="estado">Nenhum produto cadastrado.</div>
    <div v-else class="tabela-wrapper">
      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Qtd. total saída</th>
            <th>Lucro total</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in itens" :key="item.produtoId">
            <td>{{ item.codigo }}</td>
            <td>{{ item.descricao }}</td>
            <td>{{ item.quantidadeTotalSaida }}</td>
            <td>{{ formatarMoeda(item.lucroTotal) }}</td>
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
