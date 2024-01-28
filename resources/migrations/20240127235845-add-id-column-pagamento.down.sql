ALTER table public.pagamento
DROP constraint pagamento_pkey;

--;;
ALTER TABLE public.pagamento
DROP COLUMN id;

--;;
ALTER TABLE public.pagamento ADD CONSTRAINT pagamento_pkey primary key (id_pedido);
