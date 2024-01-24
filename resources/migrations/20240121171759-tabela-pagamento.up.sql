create extension if not exists "uuid-ossp";

--;;
create table pagamento (
  id_pedido uuid primary key,
  total integer NOT NULL,
  created_at timestamp default now () NOT NULL,
  constraint fk_pedido foreign key (id_pedido) references pedido (id)
);
