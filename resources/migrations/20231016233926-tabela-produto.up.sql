create table if not exists produto(
  id uuid primary key default uuid_generate_v4(),
  nome text NOT NULL,
  descricao text,
  preco_centavos integer NOT NULL,
  categoria text NOT NULL
)
