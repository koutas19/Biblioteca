CRIAÇÃO DO BANCO DE DADOS (SQLite)

No SQLite o banco é criado ao abrir o arquivo: sqlite3 biblioteca.db

PRAGMA foreign_keys = ON;

-- Remover tabelas caso existam

DROP TABLE IF EXISTS emprestimo;
DROP TABLE IF EXISTS livro;
DROP TABLE IF EXISTS usuario;

-- TABELA USUARIO

CREATE TABLE usuario (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    matricula TEXT NOT NULL UNIQUE,
    curso TEXT NOT NULL
);

-- TABELA LIVRO

CREATE TABLE livro (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    autor TEXT,
    tipo TEXT,
    disponivel INTEGER NOT NULL DEFAULT 1 CHECK (disponivel IN (0,1))
);

-- TABELA EMPRESTIMO

CREATE TABLE emprestimo (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario INTEGER NOT NULL,
    id_livro INTEGER NOT NULL,
    data_emprestimo TEXT NOT NULL DEFAULT (datetime('now')),
    data_devolucao TEXT,
    status TEXT NOT NULL DEFAULT 'ATIVO'
        CHECK (status IN ('ATIVO','FINALIZADO')),

    FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (id_livro) REFERENCES livro(id) ON DELETE CASCADE
);

-- DADOS DE TESTE

INSERT INTO usuario (nome, matricula, curso) VALUES
('Alexandre Mendes', '2024001', 'ADS'),
('José Silva', '2024002', 'Computação');

INSERT INTO livro (titulo, autor, tipo, disponivel) VALUES
('Java: Como Programar', 'Deitel & Deitel', 'Programação', 1),
('Banco de Dados', 'Ramez Elmasri', 'Banco de Dados', 1),
('Projetor Epson', 'Laboratório', 'Multimídia', 1),
('Algoritmos', 'Cormen', 'Programação', 1);

INSERT INTO emprestimo (id_usuario, id_livro, status) VALUES
(1, 1, 'ATIVO'),
(2, 2, 'FINALIZADO');