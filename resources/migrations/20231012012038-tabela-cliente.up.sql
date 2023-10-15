
create extension if not exists "uuid-ossp";
--;;
create table cliente(
    id uuid primary key default uuid_generate_v4(),
    cpf text,
    nome text,
    email text
)