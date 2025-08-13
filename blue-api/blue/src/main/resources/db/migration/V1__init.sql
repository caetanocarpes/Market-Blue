CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255)
);

CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL CHECK (preco >= 0),
    estoque INT NOT NULL DEFAULT 0 CHECK (estoque >= 0),
    categoria_id BIGINT NOT NULL REFERENCES categorias(id) ON DELETE RESTRICT
);

INSERT INTO categorias (nome, descricao) VALUES
('Hortifruti', 'Frutas, verduras e legumes frescos'),
('Bebidas', 'Sucos, refrigerantes e água'),
('Padaria', 'Pães e produtos de confeitaria');

INSERT INTO produtos (nome, descricao, preco, estoque, categoria_id) VALUES
('Maçã Verde', 'Maçã fresca', 7.99, 120, (SELECT id FROM categorias WHERE nome='Hortifruti')),
('Banana Nanica', 'Banana madura', 5.49, 200, (SELECT id FROM categorias WHERE nome='Hortifruti')),
('Suco de Uva Integral 1L', 'Sem adição de açúcar', 12.90, 80, (SELECT id FROM categorias WHERE nome='Bebidas'));
