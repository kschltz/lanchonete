# Documentação de Rotas da aplicaçao

### Produto

Rotas responsável pela manuteção de produtos no sistema;

#### Listar Produtos por categoria

- categorias: `lanche`, `sobremesa`, `bebida`, `acompanhamento`;

```cURL
curl --location 'http://localhost:8080/produtos/<categoria>'
```

Nesse caso “lanche” é a categoria buscada.

#### Criar Produto

```cURL
curl --location 'http://localhost:8080/produto' \
--header 'Content-Type: application/json' \
--data '{
    "nome": "X-Salada",
    "descricao": "Pão, alface, tomate, hamburguer e queijo",
    "categoria": "lanche",
    "preco-centavos": 1850
}'
```

#### Atualizar Produto pelo id

```cURL
curl --location --request PUT 'http://localhost:8080/produto/<id-do-produto>' \
--header 'Content-Type: application/json' \
--data '{
    "nome": "X-Salada do chefe",
    "descricao": "Pão, alface, tomate, hamburguer, queijo e molho especial do chefe.",
    "categoria": "lanche",
    "preco-centavos": 1900
}'
```

#### Deletar produto pelo id

```cURL
curl --location --request DELETE 'http://localhost:8080/produto/<id-do-produto>' \
--data ''
```

## Pedido

Rotas com definições para manutenção de pedidos no sistema;

#### Cadastrar Pedido

```cURL
curl --location 'http://localhost:8080/pedido' \
--header 'Content-Type: application/json' \
--data '{
    "id-cliente": "e938d2e6-81f2-4ca4-b597-f92f50bf1b62",
    "produtos": ["14b02996-fd6d-4688-9424-3b9ddfed4ecb"],
    "numero-do-pedido": "01",
    "total": 19000,
    "status": "aguardando pagamento"
}'
```

#### Listar Pedidos

```cURL
curl --location 'http://localhost:8080/pedidos'
```
