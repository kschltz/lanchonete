create extension if not exists "uuid-ossp";
--;;
create table if not exists pedido
(
    id               uuid primary key default uuid_generate_v4(),
    created_at       timestamp default now(),
    id_cliente       uuid    NOT NULL references cliente(id),
    numero_do_pedido text    NOT NULL,
    produtos         uuid[]  NOT NULL,
    status           text    NOT NULL,
    total            integer NOT NULL
)
