--;
ALTER TABLE public.pedido ADD COLUMN "created_at" TIMESTAMP DEFAULT now();
--
