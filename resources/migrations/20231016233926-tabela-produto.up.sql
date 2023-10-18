create table produto(
  id uuid primary key default uuid_generate_v4(),
  nome text,
  descricao text,
  preco integer,
  categoria text
)
