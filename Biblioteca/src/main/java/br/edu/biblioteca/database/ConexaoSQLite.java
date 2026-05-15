package br.edu.biblioteca.database;

import java.sql.*;

/**
 * Gerencia a conexão com o banco SQLite e cria as tabelas automaticamente.
 */
public class ConexaoSQLite {

    private static final String URL = "jdbc:sqlite:biblioteca.db";

    /** Retorna uma conexão aberta com o banco. */
    public static Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            // Ativa chaves estrangeiras no SQLite
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco SQLite: " + e.getMessage(), e);
        }
    }

    /**
     * Recria as tabelas do zero (apaga dados anteriores).
     * Útil para execuções de demonstração, garantindo estado limpo a cada run.
     */
    public static void inicializarBanco() {
        // DROP na ordem inversa para respeitar as foreign keys
        String dropEmprestimo = "DROP TABLE IF EXISTS emprestimo";
        String dropLivro      = "DROP TABLE IF EXISTS livro";
        String dropUsuario    = "DROP TABLE IF EXISTS usuario";

        String sqlUsuario = """
                CREATE TABLE IF NOT EXISTS usuario (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome       TEXT    NOT NULL,
                    matricula  TEXT    NOT NULL UNIQUE,
                    curso      TEXT    NOT NULL
                )""";

        String sqlLivro = """
                CREATE TABLE IF NOT EXISTS livro (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo     TEXT    NOT NULL,
                    autor      TEXT,
                    tipo       TEXT,
                    disponivel INTEGER DEFAULT 1   -- 1 = disponível | 0 = emprestado
                )""";

        String sqlEmprestimo = """
                CREATE TABLE IF NOT EXISTS emprestimo (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_usuario       INTEGER NOT NULL,
                    id_livro         INTEGER NOT NULL,
                    data_emprestimo  TEXT    NOT NULL,
                    data_devolucao   TEXT,
                    status           TEXT DEFAULT 'ATIVO',
                    FOREIGN KEY (id_usuario) REFERENCES usuario(id),
                    FOREIGN KEY (id_livro)   REFERENCES livro(id)
                )""";

        try (Connection conn = conectar();
             Statement st = conn.createStatement()) {

            // Limpa tabelas antigas para garantir estado limpo
            st.execute(dropEmprestimo);
            st.execute(dropLivro);
            st.execute(dropUsuario);

            st.execute(sqlUsuario);
            st.execute(sqlLivro);
            st.execute(sqlEmprestimo);
            System.out.println("[DB] Banco inicializado: biblioteca.db");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar banco: " + e.getMessage(), e);
        }
    }
}
