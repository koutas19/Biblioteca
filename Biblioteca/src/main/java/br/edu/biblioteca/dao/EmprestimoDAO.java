package br.edu.biblioteca.dao;

import br.edu.biblioteca.database.ConexaoSQLite;
import br.edu.biblioteca.model.Emprestimo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    /** Registra um empréstimo e devolve o id gerado. */
    public int registrar(Emprestimo emp) {
        String sql = "INSERT INTO emprestimo(id_usuario, id_livro, data_emprestimo, status) VALUES(?, ?, ?, ?)";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, emp.getIdUsuario());
            ps.setInt(2, emp.getIdLivro());
            ps.setString(3, emp.getDataEmprestimo().toString());
            ps.setString(4, emp.getStatus());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int id = keys.getInt(1);
                emp.setId(id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registrar empréstimo: " + e.getMessage());
        }
        return -1;
    }

    /** Empréstimo completo com atualização do livro na mesma transação. */
    public boolean emprestar(int idLivro, int idUsuario) {
        String verificarUsuario = "SELECT id FROM usuario WHERE id = ?";
        String verificarLivro = "SELECT disponivel FROM livro WHERE id = ?";
        String inserirEmprestimo = "INSERT INTO emprestimo(id_usuario, id_livro, data_emprestimo, status) VALUES(?, ?, ?, 'ATIVO')";
        String atualizarLivro = "UPDATE livro SET disponivel = 0 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUsuario = conn.prepareStatement(verificarUsuario)) {
                psUsuario.setInt(1, idUsuario);
                if (!psUsuario.executeQuery().next()) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement psLivro = conn.prepareStatement(verificarLivro)) {
                psLivro.setInt(1, idLivro);
                ResultSet rs = psLivro.executeQuery();
                if (!rs.next() || rs.getInt("disponivel") != 1) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement psEmp = conn.prepareStatement(inserirEmprestimo, Statement.RETURN_GENERATED_KEYS)) {
                psEmp.setInt(1, idUsuario);
                psEmp.setInt(2, idLivro);
                psEmp.setString(3, LocalDate.now().toString());
                psEmp.executeUpdate();
            }

            try (PreparedStatement psUp = conn.prepareStatement(atualizarLivro)) {
                psUp.setInt(1, idLivro);
                psUp.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar empréstimo completo: " + e.getMessage());
            return false;
        }
    }

    /** Registra devolução e libera o livro na mesma transação. */
    public boolean devolver(int idLivro, int idUsuario) {
        String localizarAtivo = "SELECT id FROM emprestimo WHERE id_livro = ? AND id_usuario = ? AND status = 'ATIVO' ORDER BY id DESC LIMIT 1";
        String atualizarEmprestimo = "UPDATE emprestimo SET data_devolucao = ?, status = 'FINALIZADO' WHERE id = ?";
        String atualizarLivro = "UPDATE livro SET disponivel = 1 WHERE id = ?";

        try (Connection conn = ConexaoSQLite.conectar()) {
            conn.setAutoCommit(false);

            Integer idEmprestimo = null;
            try (PreparedStatement ps = conn.prepareStatement(localizarAtivo)) {
                ps.setInt(1, idLivro);
                ps.setInt(2, idUsuario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idEmprestimo = rs.getInt("id");
                }
            }

            if (idEmprestimo == null) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(atualizarEmprestimo)) {
                ps.setString(1, LocalDate.now().toString());
                ps.setInt(2, idEmprestimo);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(atualizarLivro)) {
                ps.setInt(1, idLivro);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar devolução: " + e.getMessage());
            return false;
        }
    }

    /** Verifica se existe empréstimo ATIVO para esse livro e usuário no banco. */
    public boolean existeAtivo(int idLivro, int idUsuario) {
        String sql = "SELECT id FROM emprestimo WHERE id_livro = ? AND id_usuario = ? AND status = 'ATIVO'";
        try (Connection conn = ConexaoSQLite.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLivro);
            ps.setInt(2, idUsuario);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    /** Lista todos os empréstimos. */
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT id, id_livro, id_usuario, data_emprestimo, data_devolucao, status FROM emprestimo ORDER BY id";
        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Emprestimo e = new Emprestimo(rs.getInt("id_livro"), rs.getInt("id_usuario"));
                e.setId(rs.getInt("id"));
                e.setStatus(rs.getString("status"));
                String dataEmp = rs.getString("data_emprestimo");
                if (dataEmp != null && !dataEmp.isBlank()) {
                    e.setDataEmprestimo(LocalDate.parse(dataEmp));
                }
                String dataDev = rs.getString("data_devolucao");
                if (dataDev != null && !dataDev.isBlank()) {
                    e.setDataDevolucao(LocalDate.parse(dataDev));
                }
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar empréstimos: " + e.getMessage());
        }
        return lista;
    }

    /** Lista apenas empréstimos ativos. */
    public List<Emprestimo> listarAtivos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = "SELECT id, id_livro, id_usuario, data_emprestimo, status FROM emprestimo WHERE status = 'ATIVO' ORDER BY id";
        try (Connection conn = ConexaoSQLite.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Emprestimo e = new Emprestimo(rs.getInt("id_livro"), rs.getInt("id_usuario"));
                e.setId(rs.getInt("id"));
                e.setStatus(rs.getString("status"));
                String dataEmp = rs.getString("data_emprestimo");
                if (dataEmp != null && !dataEmp.isBlank()) {
                    e.setDataEmprestimo(LocalDate.parse(dataEmp));
                }
                lista.add(e);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar empréstimos ativos: " + e.getMessage());
        }
        return lista;
    }

    /** Lista o histórico completo, incluindo empréstimos finalizados. */
    public List<Emprestimo> listarHistorico() {
        return listarTodos();
    }
}
