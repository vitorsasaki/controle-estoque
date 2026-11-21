<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import { excluirProduto, listarProdutos } from '../api/produtosApi'
import { ApiErro } from '../api/clienteHttp'
import { TIPOS_PRODUTO, rotuloTipoProduto, type Produto, type TipoProduto } from '../tipos/estoque'

const produtos = ref<Produto[]>([])
const carregando = ref(false)
const erro = ref('')
const sucesso = ref('')
const tipo = ref<TipoProduto | ''>('')
const pagina = ref(0)
const totalPaginas = ref(0)
const totalElementos = ref(0)

async function carregar() {
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await listarProdutos({
      tipo: tipo.value,
      page: pagina.value,
    })
    produtos.value = resposta.content
    totalPaginas.value = resposta.totalPages
    totalElementos.value = resposta.totalElements
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar os produtos.'
  } finally {
    carregando.value = false
  }
}

async function remover(produto: Produto) {
  const confirmar = window.confirm(`Excluir o produto ${produto.codigo}?`)
  if (!confirmar) return
  erro.value = ''
  sucesso.value = ''
  try {
    await excluirProduto(produto.id)
    sucesso.value = `Produto ${produto.codigo} excluído.`
    await carregar()
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível excluir o produto.'
  }
}

function formatarMoeda(valor: number) {
  return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
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
      <h1>Produtos</h1>
      <div class="acoes">
        <RouterLink class="botao" to="/produtos/novo">Novo produto</RouterLink>
      </div>
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="sucesso" class="alerta sucesso">{{ sucesso }}</div>

    <div class="filtros">
      <div class="campo">
        <label for="filtro-tipo">Tipo</label>
        <select id="filtro-tipo" v-model="tipo">
          <option value="">Todos</option>
          <option v-for="item in TIPOS_PRODUTO" :key="item.valor" :value="item.valor">
            {{ item.rotulo }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="carregando" class="estado">Carregando produtos...</div>
    <div v-else-if="produtos.length === 0" class="estado">Nenhum produto encontrado.</div>
    <div v-else class="tabela-wrapper">
      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Descrição</th>
            <th>Tipo</th>
            <th>Valor fornecedor</th>
            <th>Estoque</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="produto in produtos" :key="produto.id">
            <td>{{ produto.codigo }}</td>
            <td>{{ produto.descricao }}</td>
            <td>{{ rotuloTipoProduto(produto.tipo) }}</td>
            <td>{{ formatarMoeda(Number(produto.valorFornecedor)) }}</td>
            <td>{{ produto.quantidadeEstoque }}</td>
            <td>
              <div class="acoes">
                <RouterLink class="botao secundario" :to="`/produtos/${produto.id}/editar`">
                  Editar
                </RouterLink>
                <RouterLink
                  class="botao secundario"
                  :to="`/movimentos/novo?produtoId=${produto.id}`"
                >
                  Movimentar
                </RouterLink>
                <button class="botao perigo" type="button" @click="remover(produto)">
                  Excluir
                </button>
              </div>
            </td>
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
