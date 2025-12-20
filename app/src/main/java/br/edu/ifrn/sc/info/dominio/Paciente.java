package br.edu.ifrn.sc.info.dominio;

import com.google.firebase.firestore.Exclude;

public class Paciente extends Usuario{
    private String id; // Alterado para String para ser compatível com IDs do Firestore
    private String nome;
    private String email;
    private String senha;



    public Paciente() {
    }

    // Construtor para a Lista (usado na ListaPacientesActivity)
    public Paciente(String id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }


    public Paciente(String id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
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

    // @Exclude impede que a senha seja baixada ou exibida por acidente em listas
    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
