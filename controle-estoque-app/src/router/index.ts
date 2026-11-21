import { createRouter, createWebHistory } from 'vue-router'
import ProdutosView from '../views/ProdutosView.vue'
import ProdutoFormularioView from '../views/ProdutoFormularioView.vue'
import MovimentoFormularioView from '../views/MovimentoFormularioView.vue'
import MovimentosView from '../views/MovimentosView.vue'
import ConsultaTipoView from '../views/ConsultaTipoView.vue'
import ConsultaLucroView from '../views/ConsultaLucroView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/produtos' },
    { path: '/produtos', name: 'produtos', component: ProdutosView },
    { path: '/produtos/novo', name: 'produto-novo', component: ProdutoFormularioView },
    { path: '/produtos/:id/editar', name: 'produto-editar', component: ProdutoFormularioView },
    { path: '/movimentos', name: 'movimentos', component: MovimentosView },
    { path: '/movimentos/novo', name: 'movimento-novo', component: MovimentoFormularioView },
    { path: '/consultas/tipo', name: 'consulta-tipo', component: ConsultaTipoView },
    { path: '/consultas/lucro', name: 'consulta-lucro', component: ConsultaLucroView },
  ],
})

export default router
