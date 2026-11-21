# Controle de Estoque — NEXDOM

Aplicação RESTful de controle de estoque (desafio FullStack NEXDOM), com API Spring Boot e app Vue 3.

## Visão geral e funcionalidades

- CRUD completo de produtos
- Entrada e saída de estoque com validação de saldo
- Bloqueio de exclusão de produto com movimentações (HTTP 409)
- Consulta por tipo (quantidade de saída e disponível)
- Consulta de lucro por produto (histórico com snapshot de custo)
- Histórico de movimentações
- Swagger/OpenAPI
- Testes unitários e de integração (Testcontainers)

## Requisitos de ambiente

- JDK 21 (`C:\Program Files\Java\jdk-21` no ambiente local)
- Maven 3.9+ (ou `mvnw` da API)
- Docker / Docker Compose
- Node.js 20+

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

## Estrutura

```text
controle-estoque/
├── controle-estoque-api/
├── controle-estoque-app/
├── docs/
├── docker-compose.yml
└── README.md
```

## Variáveis de ambiente

| Variável | Padrão | Uso |
|----------|--------|-----|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `estoque` | Banco |
| `DB_USER` | `estoque` | Usuário |
| `DB_PASSWORD` | `estoque` | Senha (apenas local/demo) |
| `SERVER_PORT` | `8080` | Porta da API |
| `VITE_API_URL` | vazio (usa proxy do Vite/nginx) | Base URL da API no frontend |

Paginação (em `controle-estoque-api/src/main/resources/application.yml`):

| Propriedade | Padrão | Uso |
|-------------|--------|-----|
| `spring.data.web.pageable.default-page-size` | `8` | Itens por página quando `size` não é informado |
| `spring.data.web.pageable.max-page-size` | `200` | Limite máximo aceito em `size` |

Não versionar segredos reais. Credenciais acima são apenas para desenvolvimento.

## Como executar

### 1) Stack completa (Docker Compose)

```bash
cd controle-estoque
docker compose up -d --build
```

- App Vue: `http://localhost:5173`
- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Postgres: `localhost:5432`

No container do app, o nginx serve o build estático e faz proxy de `/api` para o serviço `api`.

Somente o banco:

```bash
docker compose up -d postgres
```

### 2) API local (alternativa)

```bash
docker compose up -d postgres
cd controle-estoque-api
./mvnw spring-boot:run
```

### 3) App Vue local (alternativa)

```bash
cd controle-estoque-app
npm install
npm run dev
```

- App: `http://localhost:5173`
- Em desenvolvimento, o Vite faz proxy de `/api` para `http://localhost:8080`

## Testes

```bash
cd controle-estoque-api
./mvnw test
```

Requer Docker em execução (Testcontainers).

Cobertura principal:

- Unitários: entrada/saída, estoque insuficiente, quantidade inválida, lucro histórico
- Integração: persistência atômica, rollback em falha, concorrência sem saldo negativo, consultas agregadas

## Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/produtos` | Criar produto |
| GET | `/api/produtos` | Listar (filtro `tipo`, paginação `page`/`size`; padrão em `application.yml`) |
| GET | `/api/produtos/{id}` | Buscar |
| PUT | `/api/produtos/{id}` | Atualizar cadastro (sem saldo) |
| DELETE | `/api/produtos/{id}` | Excluir (409 se houver movimentos) |
| POST | `/api/movimentos` | Registrar entrada/saída |
| GET | `/api/movimentos` | Histórico (filtro `produtoId`, paginação `page`/`size`) |
| GET | `/api/consultas/produtos-por-tipo` | Saídas e disponibilidade (filtro `tipo`, paginação) |
| GET | `/api/consultas/lucro-por-produto` | Lucro total por produto (paginação) |

Dados de exemplo: [docs/dados-exemplo.md](docs/dados-exemplo.md)

## Modelo de dados e decisões

Ver [docs/decisoes.md](docs/documentacao.md).

Resumo:

- Lucro = `(valorVenda - custoFornecedorSnapshot) × quantidade`
- Snapshot de custo preserva histórico mesmo se o cadastro mudar
- Concorrência com `PESSIMISTIC_WRITE` + `@Transactional`
- Update de produto não altera estoque

## Critérios de aceite

- [x] SOLID pragmático, com responsabilidades claras
- [x] CRUD de produtos funcional
- [x] Entrada e saída atômicas
- [x] Estoque insuficiente com erro específico (HTTP 409)
- [x] Consistência sob concorrência (teste de integração)
- [x] Consulta por tipo com saídas e disponibilidade
- [x] Consulta de lucro por produto
- [x] Histórico de custos preservado e documentado
- [x] Testes unitários e de integração executados
- [x] Frontend responsivo com estados de loading/erro/sucesso
- [x] Swagger acessível
- [x] Banco e aplicação executáveis conforme README
- [x] Sem segredos reais no repositório


## Status

- [x] Etapa 1 — Fundação
- [x] Etapa 2 — API, regras e testes unitários
- [x] Etapa 3 — App Vue
- [x] Etapa 4 — Integração, Docker Compose, README e entrega
