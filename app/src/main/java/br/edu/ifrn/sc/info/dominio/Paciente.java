package br.edu.ifrn.sc.info.dominio;


public class Paciente  {

    private String id;
    private String nome;
    private String email;
    private String idade; // <<-- MUDADO PARA STRING, COMO VOCÊ PEDIU
    private boolean tipo; // Para diferenciar paciente de fono


    public Paciente() {
    }


    public Paciente(String id, String nome, String email, String idade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.idade = idade;
        this.tipo = false; // Pacientes sempre terão o tipo false
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public boolean isTipo() {
        return tipo;
    }

    public void setTipo(boolean tipo) {
        this.tipo = tipo;
    }
}
