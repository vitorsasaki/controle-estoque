<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { atualizarProduto, buscarProduto, criarProduto } from '../api/produtosApi'
import { ApiErro } from '../api/clienteHttp'
import { TIPOS_PRODUTO, type TipoProduto } from '../tipos/estoque'

const route = useRoute()
const router = useRouter()
const editando = computed(() => Boolean(route.params.id))
const carregando = ref(false)
const salvando = ref(false)
const erro = ref('')

const formulario = reactive({
  codigo: '',
  descricao: '',
  tipo: 'ELETRONICO' as TipoProduto,
  valorFornecedor: 0,
  quantidadeEstoque: 0,
})

async function carregar() {
  if (!editando.value) return
  carregando.value = true
  erro.value = ''
  try {
    const produto = await buscarProduto(String(route.params.id))
    formulario.codigo = produto.codigo
    formulario.descricao = produto.descricao
    formulario.tipo = produto.tipo
    formulario.valorFornecedor = Number(produto.valorFornecedor)
    formulario.quantidadeEstoque = produto.quantidadeEstoque
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível carregar o produto.'
  } finally {
    carregando.value = false
  }
}

async function salvar() {
  salvando.value = true
  erro.value = ''
  try {
    if (editando.value) {
      await atualizarProduto(String(route.params.id), {
        codigo: formulario.codigo.trim(),
        descricao: formulario.descricao.trim(),
        tipo: formulario.tipo,
        valorFornecedor: Number(formulario.valorFornecedor),
      })
    } else {
      await criarProduto({
        codigo: formulario.codigo.trim(),
        descricao: formulario.descricao.trim(),
        tipo: formulario.tipo,
        valorFornecedor: Number(formulario.valorFornecedor),
        quantidadeEstoque: Number(formulario.quantidadeEstoque),
      })
    }
    await router.push('/produtos')
  } catch (e) {
    erro.value = e instanceof ApiErro ? e.message : 'Não foi possível salvar o produto.'
  } finally {
    salvando.value = false
  }
}

onMounted(() => {
  void carregar()
})
</script>

<template>
  <section class="painel">
    <div class="cabecalho-secao">
      <h1>{{ editando ? 'Editar produto' : 'Novo produto' }}</h1>
      <button class="botao secundario" type="button" @click="router.push('/produtos')">
        Voltar
      </button>
    </div>

    <div v-if="erro" class="alerta erro">{{ erro }}</div>
    <div v-if="carregando" class="estado">Carregando formulário...</div>

    <form v-else @submit.prevent="salvar">
      <div class="grade-formulario">
        <div class="campo">
          <label for="codigo">Código</label>
          <input id="codigo" v-model="formulario.codigo" required maxlength="64" />
        </div>
        <div class="campo">
          <label for="tipo">Tipo</label>
          <select id="tipo" v-model="formulario.tipo" required>
            <option v-for="item in TIPOS_PRODUTO" :key="item.valor" :value="item.valor">
              {{ item.rotulo }}
            </option>
          </select>
        </div>
        <div class="campo" style="grid-column: 1 / -1">
          <label for="descricao">Descrição</label>
          <input id="descricao" v-model="formulario.descricao" required maxlength="255" />
        </div>
        <div class="campo">
          <label for="valorFornecedor">Valor no fornecedor</label>
          <input
            id="valorFornecedor"
            v-model.number="formulario.valorFornecedor"
            type="number"
            min="0"
            step="0.01"
            required
          />
        </div>
        <div v-if="!editando" class="campo">
          <label for="quantidadeEstoque">Quantidade inicial em estoque</label>
          <input
            id="quantidadeEstoque"
            v-model.number="formulario.quantidadeEstoque"
            type="number"
            min="0"
            step="1"
            required
          />
        </div>
        <div v-else class="campo">
          <label>Quantidade em estoque</label>
          <div class="saldo-destaque">{{ formulario.quantidadeEstoque }} (somente via movimentação)</div>
        </div>
      </div>

      <div class="acoes">
        <button class="botao" type="submit" :disabled="salvando">
          {{ salvando ? 'Salvando...' : 'Salvar' }}
        </button>
      </div>
    </form>
  </section>
</template>
