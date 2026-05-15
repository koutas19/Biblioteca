package br.edu.biblioteca.model;

import java.time.LocalDate;

public class Emprestimo {

    private int       id;
    private int       idLivro;
    private int       idUsuario;
    private LocalDate dataEmprestimo = LocalDate.now();
    private LocalDate dataDevolucao;
    private String    status = "ATIVO";

    public Emprestimo(int idLivro, int idUsuario) {
        this.idLivro   = idLivro;
        this.idUsuario = idUsuario;
    }

    // getters
    public int       getId()             { return id; }
    public int       getIdLivro()        { return idLivro; }
    public int       getIdUsuario()      { return idUsuario; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataDevolucao()  { return dataDevolucao; }
    public String    getStatus()         { return status; }

    // setters
    public void setId(int id)                          { this.id = id; }
    public void setDataEmprestimo(LocalDate d)         { this.dataEmprestimo = d; }
    public void setDataDevolucao(LocalDate d)          { this.dataDevolucao = d; }
    public void setStatus(String status)               { this.status = status; }

    @Override
    public String toString() {
        return String.format(
                "Emprestimo{id=%d, idLivro=%d, idUsuario=%d, dataEmprestimo=%s, dataDevolucao=%s, status='%s'}",
                id,
                idLivro,
                idUsuario,
                dataEmprestimo,
                dataDevolucao == null ? "-" : dataDevolucao,
                status);
    }
}
