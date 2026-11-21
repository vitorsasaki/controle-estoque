# Dados de exemplo

Use estes payloads no Swagger (`http://localhost:8080/swagger-ui.html`) ou na app Vue.

## Criar produto

```json
{
  "codigo": "ELET-001",
  "descricao": "Notebook 14 polegadas",
  "tipo": "ELETRONICO",
  "valorFornecedor": 2500.00,
  "quantidadeEstoque": 10
}
```

## Entrada

```json
{
  "produtoId": "<uuid-do-produto>",
  "tipoMovimentacao": "ENTRADA",
  "quantidade": 5,
  "valorVenda": null,
  "dataVenda": null
}
```

## Saída

```json
{
  "produtoId": "<uuid-do-produto>",
  "tipoMovimentacao": "SAIDA",
  "quantidade": 2,
  "valorVenda": 3200.00,
  "dataVenda": "2026-11-20"
}
```

## Consultas

- `GET /api/consultas/produtos-por-tipo?tipo=ELETRONICO`
- `GET /api/consultas/lucro-por-produto`
