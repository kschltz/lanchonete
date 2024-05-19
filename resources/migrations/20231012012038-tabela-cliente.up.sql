
create extension if not exists "uuid-ossp";
--;;
create table if not exists cliente(
    id uuid primary key default uuid_generate_v4(),
    cpf text unique,
    nome text,
    email text
)