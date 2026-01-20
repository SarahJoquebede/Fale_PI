package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

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

        edtEmail.setText("fono@email.com");
        edtSenha.setText("fono123");

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
        // 5. Busca o documento do usuário na coleção "usuarios" usando o ID
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 6. Verifica se o campo "tipo" é true (Fono) ou false (Paciente)
                        Boolean isFono = documentSnapshot.getBoolean("tipo");

                        // Se o campo não existir, 'isFono' será null, então tratamos como paciente por segurança.
                        if (isFono != null && isFono) {
                            // Se for Fonoaudiólogo (tipo == true)
                            // ABRE A TELA DO MENU NOVO
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                        } else {
                            // Se for Paciente (tipo == false ou o campo não existe)
                            // ABRE A TELA ANTIGA DO PACIENTE
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        finish(); // Fecha a tela de login para o usuário não voltar para ela

                    } else {
                        // Caso raro: usuário existe no Auth mas não no Firestore
                        Toast.makeText(LoginActivity.this, "Erro: Dados do usuário não encontrados.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut(); // Desloga o usuário para evitar problemas
                        btnLogin.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // Se a busca no banco falhar
                    Toast.makeText(LoginActivity.this, "Erro ao buscar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    btnLogin.setEnabled(true);
                });
    }
}
