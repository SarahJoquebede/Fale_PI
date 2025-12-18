package br.edu.ifrn.sc.info;

public class Paciente{
    private int id;
    private String nome;
    private String email;
    private String senha;

    public Paciente(){

    }
    public Paciente(String nome, String email, String senha, int id){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome(){
        return nome;
    }
    public void setNome(String nome){
        this.nome = nome;

}

    public String getEmail() {
            return email;
    }
    public void setEmail(String email) {
            this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getSenha() {
        return senha;
    }
}
