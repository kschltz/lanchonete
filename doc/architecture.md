# Documentação e Desenho da arquitetura

### Requisitos de negócio

#### Problema

Há uma lanchonete de bairro que está expandindo devido seu grande sucesso. Porém, com a expansão e sem um sistema de
controle de pedidos, o atendimento aos clientes pode ser caótico e confuso. Por exemplo, imagine que um cliente faça um
pedido complexo, como um hambúrguer personalizado com ingredientes específicos, acompanhado de batatas fritas e uma
bebida. O atendente pode anotar o pedido em um papel e entregá-lo à cozinha, mas não há garantia de que o pedido será
preparado corretamente.

Sem um sistema, pode haver confusão entre os atendentes e a cozinha, resultando em atrasos na preparação e entrega dos
pedidos. Os pedidos podem ser perdidos, mal interpretados ou esquecidos, levando à insatisfação dos clientes e à perda
de negócios.

### Solução

Com base no problema informado, a solução vai ser criar um sistema de controle de pedidos, para garantir que a
lanchonete possa atender os clientes de maneira eficiente, gerenciando seus pedidos e estoques de forma adequada:

O sistema precisa ter:

- Gerenciamento de estoque dos produtos: O sistema deve ser capaz de gerenciar o estoque de produtos a venda da
  lanchonete.
- Controle de pedidos: O sistema deve permitir que os clientes façam pedidos sem interagir com um atendente, podendo
  escolher até 3 items nesse pedido.
- Eficiência operacional: O sistema deve garantir que os pedidos sejam preparados e entregues de forma eficiente,
  minimizando atrasos e erros.
- Atendimento ao cliente: O sistema deve garantir que os clientes sejam atendidos de maneira eficiente e que seus
  pedidos sejam preparados corretamente informando em tempo real o andamento do pedido e notificando o cliente quando o
  pedido esta pronto.

Abaixo está os diagramas de infraestrutura desse sistema.

### Infraestrutura na nuvem

![img.png](aws-infra-diagram.png)

### Kubernetes

#### Serviço

![img_1.png](k8s-service.png)

#### Banco de Dados

![img_2.png](k8s-database.png)

#### Serviços externos

O serviço principal (Lanchonete)  , comunica-se via mensageria com o serviço de pagamento e o serviço de preparo de
pedidos. A comunicação é feita através de um broker NATS detalhada abaixo.

```mermaid
sequenceDiagram
    participant C as Cliente
    participant L as Lanchonete
    participant N as NATS Broker
    participant S as Pagamento
    participant P as Preparo
    C ->>+ L: 1. Solicitar pagamento (conclusão do pedido)
    L ->> N: 2. pagamento.novo-pedido
    N ->> S: 3. pagamento.novo-pedido(processar pedido de pagamento)
    S ->> N: 4. pagamento.status
    loop Processar status
        N ->> L: 5. pagamento.status (persiste status do pagamento)
    end
    L ->> N: 6. pedido.novo-preparo (pedido pronto para preparo)
    N ->> P: 7. pedido.novo-preparo (pedido pronto para preparo)
    P ->> N: 8. pedido.status (pedido em preparo)
    loop Processar status
        N ->> L: 9. pedido.status (persiste status do pedido)
    end
    N ->> L: 10. pedido.status (pedido pronto)

```