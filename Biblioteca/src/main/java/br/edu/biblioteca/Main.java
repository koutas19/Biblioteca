package br.edu.biblioteca;

import br.edu.biblioteca.controller.BibliotecaController;
import br.edu.biblioteca.database.ConexaoSQLite;

public class Main {

    public static void main(String[] args) {

        // Prepara o banco do zero
        ConexaoSQLite.inicializarBanco();
        BibliotecaController c = new BibliotecaController();

        // ------------------------------------------------------------------ //
        // TESTE 1: Cadastrar livros
        // ------------------------------------------------------------------ //
        int java     = c.cadastrarLivro("Java: Como Programar", "Deitel & Deitel", "Programação");
        int banco    = c.cadastrarLivro("Banco de Dados", "Ramez Elmasri", "Banco de Dados");
        int projetor = c.cadastrarLivro("Projetor Epson", "Laboratório", "Multimídia");

        testar("Livro Java salvo no banco",     c.livroExisteNoBanco(java));
        testar("Livro Banco salvo no banco",    c.livroExisteNoBanco(banco));
        testar("Livro Projetor salvo no banco", c.livroExisteNoBanco(projetor));

        // ------------------------------------------------------------------ //
        // TESTE 2: Cadastrar usuários
        // ------------------------------------------------------------------ //
        int alex = c.cadastrarUsuario("Alexandre Mendes", "2024001", "ADS");
        int jose = c.cadastrarUsuario("José Silva",       "2024002", "Computação");

        testar("Alexandre salvo no banco", c.usuarioExisteNoBanco(alex));
        testar("José salvo no banco",      c.usuarioExisteNoBanco(jose));

        // ------------------------------------------------------------------ //
        // TESTE 3: Matrícula repetida não pode criar um segundo cadastro
        // ------------------------------------------------------------------ //
        int alexDuplicado = c.cadastrarUsuario("Alexandre Mendes", "2024001", "ADS");
        testar("Matrícula duplicada retorna id existente", alexDuplicado == alex);

        // ------------------------------------------------------------------ //
        // TESTE 4: Empréstimo válido
        // ------------------------------------------------------------------ //
        boolean emprestimo1 = c.emprestar(java, alex);
        boolean emprestimo2 = c.emprestar(projetor, jose);

        testar("Empréstimo Java→Alexandre realizado", emprestimo1);
        testar("Empréstimo Projetor→José realizado", emprestimo2);
        testar("Livro Java ficou indisponível no banco",     c.livroEstaIndisponivel(java));
        testar("Livro Projetor ficou indisponível no banco", c.livroEstaIndisponivel(projetor));

        // ------------------------------------------------------------------ //
        // TESTE 5: Livro já emprestado não pode sair de novo
        // ------------------------------------------------------------------ //
        boolean bloqueou = !c.emprestar(java, jose);
        testar("Livro já emprestado foi bloqueado",          bloqueou);
        testar("Empréstimo indevido não foi salvo no banco",  !c.emprestimoExisteNoBanco(java, jose));

        // ------------------------------------------------------------------ //
        // TESTE 6: Livro que não existe não pode ser emprestado
        // ------------------------------------------------------------------ //
        boolean bloqueouInexistente = !c.emprestar(9999, alex);
        testar("Livro inexistente foi bloqueado",            bloqueouInexistente);
        testar("Empréstimo indevido não foi salvo no banco", !c.emprestimoExisteNoBanco(9999, alex));

        // ------------------------------------------------------------------ //
        // TESTE 7: Devolução válida
        // ------------------------------------------------------------------ //
        boolean devolucao = c.devolver(java, alex);
        testar("Devolução do livro Java realizada", devolucao);
        testar("Livro Java voltou a ficar disponível", !c.livroEstaIndisponivel(java));

        // ------------------------------------------------------------------ //
        // TESTE 8: Devolução inválida
        // ------------------------------------------------------------------ //
        boolean devolucaoInvalida = !c.devolver(java, alex);
        testar("Segunda devolução foi bloqueada", devolucaoInvalida);

        // ------------------------------------------------------------------ //
        // Estado final salvo no banco
        // ------------------------------------------------------------------ //
        System.out.println("\n===== BANCO DE DADOS =====");
        c.listarUsuarios();
        c.listarLivros();
        c.listarLivrosDisponiveis();
        c.listarEmprestimos();
        c.listarEmprestimosAtivos();
        c.listarHistoricoEmprestimos();
    }

    // Imprime PASSOU ou FALHOU para cada teste
    static void testar(String descricao, boolean ok) {
        System.out.printf("%-50s %s%n", descricao, ok ? "✔ PASSOU" : "✘ FALHOU");
    }
}
