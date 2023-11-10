-- create table USERS (
--   id SERIAL PRIMARY KEY UNIQUE NOT NULL,
--   nome VARCHAR(50) UNIQUE NOT NULL,
--   chave_publica BYTEA UNIQUE NOT NULL,
-- );

create table pilacoin (
  id SERIAL PRIMARY KEY NOT NULL,
  data_criacao DATE NOT NULL,
  chave_criador BYTEA NOT NULL,
  nome_criador VARCHAR(50) NOT NULL,
  nonce TEXT NOT NULL,
  status VARCHAR(50) NOT NULL
);
