<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { consultarProdutosPorTipo } from '../api/consultasApi'
import { ApiErro } from '../api/clienteHttp'
import { TIPOS_PRODUTO, rotuloTipoProduto, type ProdutoPorTipo, type TipoProduto } from '../tipos/estoque'

const itens = ref<ProdutoPorTipo[]>([])
const tipo = ref<TipoProduto | ''>('')
const carregando = ref(false)
const erro = ref('')
const pagina = ref(0)
const totalPaginas = ref(0)
const totalElementos = ref(0)

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await consultarProdutosPorTipo({
      tipo: tipo.value,
      page: pagina.value,
    })
    itens.value = resposta.content
    totalPaginas.value = resposta.totalPages
    totalElementos.value = resposta.totalElements
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar a consulta.'
  } finally {
    carregando.value = false
  }
}

watch(tipo, () => {
  pagina.value = 0
  void carregar()
})

onMounted(() => {
  void carregar()
})
</script>

<template>
  <section class="painel">
    <div class="cabecalho-secao">
      <h1>Consulta por tipo</h1>
    </div>

    <div class="filtros">
      <div class="campo">
        <label for="tipo">Tipo</label>
        <select id="tipo" v-model="tipo">
          <option value="">Todos</option>
          <option v-for="item in TIPOS_PRODUTO" :key="item.valor" :value="item.valor">
            {{ item.rotulo }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="carregando" class="estado">Carregando consulta...</div>
    <div v-else-if="itens.length === 0" class="estado">Nenhum produto para o filtro informado.</div>
    <div v-else class="tabela-wrapper">
      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Tipo</th>
            <th>Qtd. saída</th>
            <th>Qtd. disponível</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in itens" :key="item.produtoId">
            <td>{{ item.codigo }}</td>
            <td>{{ item.descricao }}</td>
            <td>{{ rotuloTipoProduto(item.tipo) }}</td>
            <td>{{ item.quantidadeSaida }}</td>
            <td>{{ item.quantidadeDisponivel }}</td>
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
