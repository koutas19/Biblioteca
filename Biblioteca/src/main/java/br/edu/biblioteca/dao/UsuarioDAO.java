package br.edu.biblioteca.dao;

import br.edu.biblioteca.database.ConexaoSQLite;
import br.edu.biblioteca.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /** Insere um usuário. Se a matrícula já existir, retorna o id existente. */
    public int inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario(nome, matricula, curso) VALUES(?, ?, ?)";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getMatricula());
            ps.setString(3, usuario.getCurso());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                usuario.setId(id);
                return id;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                return buscarIdPorMatricula(usuario.getMatricula());
            }
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
        }
        return -1;
    }

    /** Verifica se o usuário existe no banco pelo id. */
    public boolean existePorId(int id) {
        String sql = "SELECT id FROM usuario WHERE id = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Retorna o id de um usuário pela matrícula, ou -1 se não encontrado. */
    private int buscarIdPorMatricula(String matricula) {
        String sql = "SELECT id FROM usuario WHERE matricula = ?";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricula);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por matrícula: " + e.getMessage());
        }
        return -1;
    }

    /** Lista todos os usuários. */
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nome, matricula, curso FROM usuario ORDER BY id";
        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario(rs.getString("nome"), rs.getString("matricula"), rs.getString("curso"));
                u.setId(rs.getInt("id"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }
}
