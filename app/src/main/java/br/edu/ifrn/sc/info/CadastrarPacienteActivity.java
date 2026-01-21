package br.edu.ifrn.sc.info;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastrarPacienteActivity extends AppCompatActivity {

    private EditText etdNome, etdEmail, etdSenha, etdIdade;
    private Button btnCadastrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_paciente);

        // 1. Inicializa o Firebase Auth e Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 2. Conecta as views do layout
        etdNome = findViewById(R.id.etdNome);
        etdEmail = findViewById(R.id.edtEmail);
        etdSenha = findViewById(R.id.edtSenha);
        etdIdade = findViewById(R.id.edtIdade);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // 3. Configura o clique do botão
        btnCadastrar.setOnClickListener(v -> {
            // Pega os dados dos campos de texto
            String nome = etdNome.getText().toString().trim();
            String email = etdEmail.getText().toString().trim();
            String senha = etdSenha.getText().toString().trim();
            String idade = etdIdade.getText().toString().trim();

            // Validação simples (pode ser melhorada)
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || idade.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chama a função para registrar o usuário
            registrarUsuario(nome, email, senha, idade);
        });
    }

    private void registrarUsuario(String nome, String email, String senha, String idade) {
        // Passo A: Cria o usuário no Firebase Authentication (para login com e-mail/senha)
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, taskAuth -> {
                    if (taskAuth.isSuccessful()) {
                        // Se o usuário foi criado no Authentication com sucesso...
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();

                        // Passo B: Salva as informações adicionais no Firestore
                        salvarDadosDoPaciente(uid, nome, email, idade);
                    } else {
                        // Se a criação no Authentication falhou (ex: e-mail já existe, senha fraca)
                        Toast.makeText(CadastrarPacienteActivity.this, "Falha ao criar usuário: " + taskAuth.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void salvarDadosDoPaciente(String uid, String nome, String email, String idade) {
        // Cria um objeto Map para guardar os dados
        Map<String, Object> pacienteData = new HashMap<>();
        pacienteData.put("nome", nome);
        pacienteData.put("email", email);
        pacienteData.put("idade", idade);

        // CORREÇÃO 1: ADICIONA O CAMPO 'tipo' PARA O LOGIN FUNCIONAR
        pacienteData.put("tipo", false); // Essencial para identificar como paciente

        // CORREÇÃO 2: SALVA NA COLEÇÃO "pacientes"
        // Esta linha vai criar a coleção "pacientes" na primeira vez que for executada.
        db.collection("pacientes").document(uid).set(pacienteData)
                .addOnSuccessListener(aVoid -> {
                    // Se os dados foram salvos no Firestore com sucesso...
                    Toast.makeText(CadastrarPacienteActivity.this, "Paciente cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha a tela de cadastro e volta para a anterior
                })
                .addOnFailureListener(e -> {
                    // Se falhou ao salvar no Firestore (ex: problema de permissão)
                    Toast.makeText(CadastrarPacienteActivity.this, "Erro ao salvar dados do paciente: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
