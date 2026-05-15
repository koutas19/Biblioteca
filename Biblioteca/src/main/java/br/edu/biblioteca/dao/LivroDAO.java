package br.edu.biblioteca.dao;

import br.edu.biblioteca.database.ConexaoSQLite;
import br.edu.biblioteca.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    /** Insere um livro e retorna o id gerado. */
    public int inserir(Livro livro) {
        String sql = "INSERT INTO livro(titulo, autor, tipo, disponivel) VALUES(?, ?, ?, 1)";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getAutor());
            ps.setString(3, livro.getTipo());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                livro.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir livro: " + e.getMessage());
        }
        return -1;
    }

    /** Verifica se o livro está disponível para empréstimo. */
    public boolean disponivel(int id) {
        String sql = "SELECT disponivel FROM livro WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt("disponivel") == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao verificar disponibilidade: " + e.getMessage());
            return false;
        }
    }

    /** Verifica se o livro existe no banco pelo id. */
    public boolean existePorId(int id) {
        String sql = "SELECT id FROM livro WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Atualiza a disponibilidade do livro. */
    public void atualizarDisponibilidade(int id, boolean disponivel) {
        String sql = "UPDATE livro SET disponivel = ? WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, disponivel ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
        }
    }

    /** Lista todos os livros. */
    public List<Livro> listarTodos() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, tipo, disponivel FROM livro ORDER BY id";
        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Livro l = new Livro(rs.getString("titulo"), rs.getString("autor"), rs.getString("tipo"));
                l.setId(rs.getInt("id"));
                l.setDisponivel(rs.getInt("disponivel") == 1);
                lista.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        return lista;
    }

    /** Lista apenas livros disponíveis. */
    public List<Livro> listarDisponiveis() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, tipo, disponivel FROM livro WHERE disponivel = 1 ORDER BY id";
        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Livro l = new Livro(rs.getString("titulo"), rs.getString("autor"), rs.getString("tipo"));
                l.setId(rs.getInt("id"));
                l.setDisponivel(true);
                lista.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros disponíveis: " + e.getMessage());
        }
        return lista;
    }
}
