# Decisões de negócio e arquitetura

## Premissas

- Exclusão de produto é bloqueada quando existem movimentações (HTTP 409), sem cascade delete.
- Em entradas, `valorVenda` e `dataVenda` permanecem nulos; `dataMovimentacao` é preenchida automaticamente.
- Em saídas, o custo do fornecedor vigente é gravado em `custoFornecedorSnapshot` para preservar o lucro histórico.
- Lucro bruto simplificado: `(valorVenda - custoFornecedorSnapshot) × quantidade`. Sem impostos ou despesas.
- Após o cadastro, o saldo de estoque só muda por movimentação. O update de produto não altera quantidade.
- Movimentações são imutáveis nesta versão (sem edição, exclusão ou estorno).
- Concorrência em entrada/saída usa `PESSIMISTIC_WRITE` na leitura do produto, dentro da mesma `@Transactional`.

## Modelo de dados

- `produtos`: id, codigo (único), descricao, tipo, valor_fornecedor, quantidade_estoque, criado_em, atualizado_em
- `movimentos_estoque`: id, produto_id, tipo_movimentacao, valor_venda, data_venda, custo_fornecedor_snapshot, quantidade, data_movimentacao
- Tipos de produto: `ELETRONICO`, `ELETRODOMESTICO`, `MOVEL`
- Tipos de movimentação: `ENTRADA`, `SAIDA`
- Constraints impedem saldo negativo e inconsistência de campos de venda em entradas/saídas

## Cálculo de lucro e histórico

- O lucro é calculado apenas sobre saídas.
- O custo usado é o snapshot da operação, não o valor atual do cadastro.
- Alterar `valorFornecedor` depois da venda não muda lucros já registrados.
- O indicador é lucro bruto simplificado (sem impostos, frete ou despesas).

## Estratégia de concorrência

- `ProdutoRepository.findByIdForUpdate` usa `LockModeType.PESSIMISTIC_WRITE`.
- Entrada e saída ocorrem em `@Transactional`.
- Duas saídas simultâneas competem pelo mesmo lock; a segunda vê o saldo já atualizado.
- Teste de integração cobre o cenário de duas saídas concorrentes sem saldo negativo.

## Arquitetura e SOLID

- Organização por feature: `produto`, `movimento`, `consulta`, `shared`
- SRP: controllers HTTP, services de aplicação, processador/calculadora de domínio, repositories
- OCP: processamento de entrada/saída encapsulado em `ProcessadorMovimentoEstoque`
- DIP: serviços dependem de repositories via construtor; DTOs separados das entidades JPA
- Sem autenticação, microserviços ou mensageria nesta entrega

## Paginação

- Listagens de produtos, movimentos e consultas usam `Page`/`Pageable` do Spring (`page`, `size`).
- Tamanho padrão e máximo ficam em `application.yml` (`spring.data.web.pageable.default-page-size` / `max-page-size`).
- O app Vue consome o contrato `Pagina<T>` e exibe controles Anterior/Próxima nas tabelas.

## Stack

- Java 21 LTS, Spring Boot 4.1.2
- PostgreSQL 16 + Flyway
- Vue 3 + TypeScript + Vite (`controle-estoque-app`)
- Testcontainers para testes de integração
- Docker Compose para Postgres, API e app Vue (nginx + proxy `/api`)



