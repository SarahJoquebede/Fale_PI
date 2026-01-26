package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import br.edu.ifrn.sc.info.databinding.ActivityTesteMenuBinding;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtSenha;
    private Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Inicializa o Firebase Auth e o Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Conecta os componentes do XML com o Java
        edtEmail = findViewById(R.id.etEmail);
        edtSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);

        //Para facilitar os testes
        //edtEmail.setText("paci@email.com");
        //edtSenha.setText("paci123");

        // 3. Configura o clique do botão de login
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String senha = edtSenha.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            } else {
                fazerLogin(email, senha);
            }
        });
    }

    private void fazerLogin(String email, String senha) {
        btnLogin.setEnabled(false); // Desabilita o botão para evitar cliques duplos

        // 4. Tenta fazer o login com o e-mail e senha fornecidos
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {
                    // Se o login for bem-sucedido, pega o ID do usuário
                    String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                    // Chama a função que vai verificar o tipo de usuário
                    verificarTipoUsuario(uid);

                })
                .addOnFailureListener(e -> {
                    // Se o login falhar, exibe uma mensagem de erro
                    Toast.makeText(LoginActivity.this, "Erro no login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true); // Reabilita o botão
                });
    }

    private void verificarTipoUsuario(String uid) {
        // 1. Tenta buscar na coleção de pacientes primeiro (já que é onde está o problema)
        db.collection("pacientes").document(uid).get()
                .addOnSuccessListener(docPaciente -> {
                    if (docPaciente.exists()) {
                        Log.d("LOGIN_DEBUG", "Sucesso! Encontrado na coleção PACIENTES");
                        startActivity(new Intent(LoginActivity.this, ListaAtividadesActivity.class));
                        finish();
                    } else {
                        Log.d("LOGIN_DEBUG", "Não está em pacientes. Tentando coleção USUARIOS...");

                        // 2. Se não achou em pacientes, tenta na de usuários (Fonos)
                        db.collection("usuarios").document(uid).get()
                                .addOnSuccessListener(docFono -> {
                                    if (docFono.exists()) {
                                        Log.d("LOGIN_DEBUG", "Sucesso! Encontrado na coleção USUARIOS");
                                        startActivity(new Intent(LoginActivity.this, MenuTeste.class));
                                        finish();
                                    } else {
                                        Log.e("LOGIN_DEBUG", "ERRO: O UID " + uid + " não existe em NENHUMA coleção!");
                                        Toast.makeText(this, "Erro: Cadastro incompleto no banco de dados.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        btnLogin.setEnabled(true);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LOGIN_DEBUG", "Erro de conexão com Firestore: " + e.getMessage());
                    btnLogin.setEnabled(true);
                });
    }

    }


