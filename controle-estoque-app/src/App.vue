<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="layout">
    <header class="topo">
      <div class="marca">
        Controle de Estoque
        <span>NEXDOM · gestão de produtos e movimentações</span>
      </div>
      <nav class="nav">
        <RouterLink to="/produtos">Produtos</RouterLink>
        <RouterLink to="/movimentos">Movimentações</RouterLink>
        <RouterLink to="/movimentos/novo">Entrada/Saída</RouterLink>
        <RouterLink to="/consultas/tipo">Consulta Por tipo</RouterLink>
        <RouterLink to="/consultas/lucro">Consulta Lucro</RouterLink>
      </nav>
    </header>
    <main>
      <RouterView />
    </main>
  </div>
</template>
