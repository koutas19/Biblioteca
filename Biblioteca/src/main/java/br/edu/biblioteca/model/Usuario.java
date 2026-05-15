package br.edu.biblioteca.model;

public class Usuario {

    private int    id;
    private String nome;
    private String matricula;
    private String curso;

    public Usuario(String nome, String matricula, String curso) {
        this.nome      = nome;
        this.matricula = matricula;
        this.curso     = curso;
    }

    // getters
    public int    getId()        { return id; }
    public String getNome()      { return nome; }
    public String getMatricula() { return matricula; }
    public String getCurso()     { return curso; }

    // setter
    public void setId(int id) { this.id = id; }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nome='%s', matricula='%s', curso='%s'}",
                id, nome, matricula, curso);
    }
}
