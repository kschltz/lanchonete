# Documentação de Rotas da aplicação

### Recomendação de ordem para execução das APIs

1. Cadastrar um cliente na API de cliente (Opcional)
2. Cadastrar pelo menos 1 produto na [API de Produto](#produto)
3. Criar um pedido na [API de pedidos](#pedido) (de preferência, utilize o ID do cliente e do produto criado anteriormente).

**Ps. Caso rode a aplicação via minikube, é necessário usar o external IP do minikube como host**

### Produto

Rotas responsável pela manutenção de produtos no sistema;

#### Listar Produtos por categoria

- categorias: `lanche`, `sobremesa`, `bebida`, `acompanhamento`;

```cURL
curl --location 'http://10.123.3.156:8080/produtos/lanche'
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

- possíveis status: `aguardando-pagamento`, `recebido`, `em-preparo`, `pronto`, `finalizado`;

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

#### Atualizar Pedidos

```cURL
curl --location PUT 'http://localhost:8080/pedido/{{pedido_id}}'
--header 'Content-Type: application/json'
--data '{
    "id-cliente": "{{cliente_id}}",
    "produtos": ["{{product_id}}"],
    "numero-do-pedido": "01",
    "total": 19000,
    "status": "aguardando pagamento"
}'
```

#### Autenticar um usuário

```cURL
curl --location 'http://localhost:8080/autenticar' \
--header 'Content-Type: application/json' \
--data '{
    "cpf": "{{cpf}}",
    "password": "{{password}}"
}'
```


#### Deletar todos os dados de um usuário/cliente

```cURL
curl --location --request DELETE 'http://localhost:8080/cliente/<cpf-do-cliente>' \
--data ''
```