package br.edu.biblioteca.model;

public class Livro {

    private int     id;
    private String  titulo;
    private String  autor;
    private String  tipo;
    private boolean disponivel = true;

    public Livro(String titulo, String autor) {
        this(titulo, autor, "Geral");
    }

    public Livro(String titulo, String autor, String tipo) {
        this.titulo = titulo;
        this.autor  = autor;
        this.tipo   = tipo;
    }

    // getters
    public int     getId()         { return id; }
    public String  getTitulo()     { return titulo; }
    public String  getAutor()      { return autor; }
    public String  getTipo()       { return tipo; }
    public boolean isDisponivel()  { return disponivel; }

    // setters
    public void setId(int id)                    { this.id = id; }
    public void setDisponivel(boolean disponivel){ this.disponivel = disponivel; }

    @Override
    public String toString() {
        return String.format("Livro{id=%d, titulo='%s', autor='%s', disponivel=%s}",
                id, titulo, autor, disponivel ? "sim" : "não");
    }
}
