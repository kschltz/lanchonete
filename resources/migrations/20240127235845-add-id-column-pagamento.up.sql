ALTER TABLE pagamento
DROP constraint pagamento_pkey;

--;;
DROP INDEX IF EXISTS pagamento_pkey;

--;;
ALTER TABLE public.pagamento
ADD COLUMN "id" uuid primary key default uuid_generate_v4 ();

--;;
