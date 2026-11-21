<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { listarProdutos } from '../api/produtosApi'
import { registrarMovimento } from '../api/movimentosApi'
import { ApiErro } from '../api/clienteHttp'
import type { Produto, TipoMovimentacao } from '../tipos/estoque'

const route = useRoute()
const router = useRouter()
const produtos = ref<Produto[]>([])
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')
const sucesso = ref('')

const formulario = reactive({
  produtoId: '',
  tipoMovimentacao: 'ENTRADA' as TipoMovimentacao,
  quantidade: 1,
  valorVenda: 0,
  dataVenda: new Date().toISOString().slice(0, 10),
})

const produtoSelecionado = computed(() =>
  produtos.value.find((item) => item.id === formulario.produtoId),
)

const ehSaida = computed(() => formulario.tipoMovimentacao === 'SAIDA')

async function carregarProdutos() {
  carregando.value = true
  erro.value = ''
  try {
    const resposta = await listarProdutos({ page: 0, size: 200 })
    produtos.value = resposta.content
    const produtoIdQuery = typeof route.query.produtoId === 'string' ? route.query.produtoId : ''
    if (produtoIdQuery && produtos.value.some((p) => p.id === produtoIdQuery)) {
      formulario.produtoId = produtoIdQuery
    }
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar os produtos.'
  } finally {
    carregando.value = false
  }
}

async function salvar() {
  if (!formulario.produtoId) {
    erro.value = 'Selecione um produto.'
    return
  }
  if (formulario.quantidade <= 0) {
    erro.value = 'A quantidade deve ser maior que zero.'
    return
  }

  salvando.value = true
  erro.value = ''
  sucesso.value = ''
  try {
    await registrarMovimento({
      produtoId: formulario.produtoId,
      tipoMovimentacao: formulario.tipoMovimentacao,
      quantidade: Number(formulario.quantidade),
      valorVenda: ehSaida.value ? Number(formulario.valorVenda) : null,
      dataVenda: ehSaida.value ? formulario.dataVenda : null,
    })
    sucesso.value = 'Movimentação registrada com sucesso.'
    await carregarProdutos()
    formulario.quantidade = 1
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível registrar a movimentação.'
  } finally {
    salvando.value = false
  }
}

watch(
  () => formulario.tipoMovimentacao,
  (tipo) => {
    if (tipo === 'ENTRADA') {
      formulario.valorVenda = 0
    }
  },
)

onMounted(() => {
  void carregarProdutos()
})
</script>

<template>
  <section class="painel">
    <div class="cabecalho-secao">
      <h1>Registrar entrada/saída</h1>
      <button class="botao secundario" type="button" @click="router.push('/movimentos')">
        Ver histórico
      </button>
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="sucesso" class="alerta sucesso">{{ sucesso }}</div>
    <div v-if="carregando" class="estado">Carregando produtos...</div>
    <div v-else-if="produtos.length === 0" class="estado">
      Cadastre um produto antes de movimentar o estoque.
    </div>

    <form v-else @submit.prevent="salvar">
      <div class="grade-formulario">
        <div class="campo">
          <label for="produtoId">Produto</label>
          <select id="produtoId" v-model="formulario.produtoId" required>
            <option value="" disabled>Selecione um produto</option>
            <option v-for="produto in produtos" :key="produto.id" :value="produto.id">
              {{ produto.codigo }} — {{ produto.descricao }}
            </option>
          </select>
        </div>
        <div class="campo">
          <label>Saldo atual</label>
          <div class="saldo-destaque">
            <template v-if="produtoSelecionado">
              {{ produtoSelecionado.quantidadeEstoque }} unidade(s)
            </template>
            <template v-else>—</template>
          </div>
        </div>
        <div class="campo">
          <label for="tipoMovimentacao">Tipo de movimentação</label>
          <select id="tipoMovimentacao" v-model="formulario.tipoMovimentacao" required>
            <option value="ENTRADA">Entrada</option>
            <option value="SAIDA">Saída</option>
          </select>
        </div>
        <div class="campo">
          <label for="quantidade">Quantidade</label>
          <input
            id="quantidade"
            v-model.number="formulario.quantidade"
            type="number"
            min="1"
            step="1"
            required
          />
        </div>
        <template v-if="ehSaida">
          <div class="campo">
            <label for="valorVenda">Valor de venda (unitário)</label>
            <input
              id="valorVenda"
              v-model.number="formulario.valorVenda"
              type="number"
              min="0"
              step="0.01"
              required
            />
          </div>
          <div class="campo">
            <label for="dataVenda">Data de venda</label>
            <input id="dataVenda" v-model="formulario.dataVenda" type="date" required />
          </div>
        </template>
      </div>

      <div v-if="ehSaida" class="alerta aviso">
        Em saídas, o custo do fornecedor vigente será registrado no histórico para cálculo de lucro.
      </div>

      <div class="acoes">
        <button class="botao" type="submit" :disabled="salvando">
          {{ salvando ? 'Registrando...' : 'Confirmar movimentação' }}
        </button>
      </div>
    </form>
  </section>
</template>
