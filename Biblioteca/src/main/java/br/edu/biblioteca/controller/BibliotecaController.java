package br.edu.biblioteca.controller;

import br.edu.biblioteca.dao.EmprestimoDAO;
import br.edu.biblioteca.dao.LivroDAO;
import br.edu.biblioteca.dao.UsuarioDAO;
import br.edu.biblioteca.model.Emprestimo;
import br.edu.biblioteca.model.Livro;
import br.edu.biblioteca.model.Usuario;

public class BibliotecaController {

    private final LivroDAO      livroDAO   = new LivroDAO();
    private final UsuarioDAO    usuarioDAO = new UsuarioDAO();
    private final EmprestimoDAO empDAO     = new EmprestimoDAO();

    // ------------------------------------------------------------------ //
    //  Cadastros
    // ------------------------------------------------------------------ //

    public int cadastrarLivro(String titulo, String autor, String tipo) {
        Livro livro = new Livro(titulo, autor, tipo);
        int id = livroDAO.inserir(livro);
        System.out.printf("[Livro] Cadastrado: %s (id=%d)%n", titulo, id);
        return id;
    }

    public int cadastrarUsuario(String nome, String matricula, String curso) {
        Usuario usuario = new Usuario(nome, matricula, curso);
        int id = usuarioDAO.inserir(usuario);
        System.out.printf("[Usuário] Cadastrado: %s – %s (id=%d)%n", nome, matricula, id);
        return id;
    }

    // ------------------------------------------------------------------ //
    //  Empréstimo e devolução
    // ------------------------------------------------------------------ //

    public boolean emprestar(int idLivro, int idUsuario) {
        if (!usuarioDAO.existePorId(idUsuario)) {
            System.out.printf("[Empréstimo] Usuário id=%d não existe.%n", idUsuario);
            return false;
        }
        if (!livroDAO.existePorId(idLivro)) {
            System.out.printf("[Empréstimo] Livro id=%d não existe.%n", idLivro);
            return false;
        }
        if (!livroDAO.disponivel(idLivro)) {
            System.out.printf("[Empréstimo] Livro id=%d está INDISPONÍVEL.%n", idLivro);
            return false;
        }

        boolean ok = empDAO.emprestar(idLivro, idUsuario);
        if (ok) {
            System.out.printf("[Empréstimo] Registrado! (livro=%d, usuário=%d)%n", idLivro, idUsuario);
        }
        return ok;
    }

    public boolean devolver(int idLivro, int idUsuario) {
        if (!usuarioDAO.existePorId(idUsuario)) {
            System.out.printf("[Devolução] Usuário id=%d não existe.%n", idUsuario);
            return false;
        }
        if (!livroDAO.existePorId(idLivro)) {
            System.out.printf("[Devolução] Livro id=%d não existe.%n", idLivro);
            return false;
        }

        boolean ok = empDAO.devolver(idLivro, idUsuario);
        if (ok) {
            System.out.printf("[Devolução] Registrada! (livro=%d, usuário=%d)%n", idLivro, idUsuario);
        } else {
            System.out.printf("[Devolução] Nenhum empréstimo ativo encontrado para livro=%d e usuário=%d.%n", idLivro, idUsuario);
        }
        return ok;
    }

    // ------------------------------------------------------------------ //
    //  Verificações diretas no banco (usadas nos testes)
    // ------------------------------------------------------------------ //

    public boolean livroExisteNoBanco(int id) {
        return livroDAO.existePorId(id);
    }

    public boolean usuarioExisteNoBanco(int id) {
        return usuarioDAO.existePorId(id);
    }

    public boolean livroEstaIndisponivel(int id) {
        return !livroDAO.disponivel(id);
    }

    public boolean emprestimoExisteNoBanco(int idLivro, int idUsuario) {
        return empDAO.existeAtivo(idLivro, idUsuario);
    }

    // ------------------------------------------------------------------ //
    //  Relatórios
    // ------------------------------------------------------------------ //

    public void listarLivros() {
        System.out.println("\n=== LIVROS ===");
        livroDAO.listarTodos().forEach(System.out::println);
    }

    public void listarLivrosDisponiveis() {
        System.out.println("\n=== LIVROS DISPONÍVEIS ===");
        livroDAO.listarDisponiveis().forEach(System.out::println);
    }

    public void listarUsuarios() {
        System.out.println("\n=== USUÁRIOS ===");
        usuarioDAO.listarTodos().forEach(System.out::println);
    }

    public void listarEmprestimos() {
        System.out.println("\n=== EMPRÉSTIMOS ===");
        empDAO.listarTodos().forEach(System.out::println);
    }

    public void listarEmprestimosAtivos() {
        System.out.println("\n=== EMPRÉSTIMOS ATIVOS ===");
        empDAO.listarAtivos().forEach(System.out::println);
    }

    public void listarHistoricoEmprestimos() {
        System.out.println("\n=== HISTÓRICO DE EMPRÉSTIMOS ===");
        empDAO.listarHistorico().forEach(System.out::println);
    }
}
