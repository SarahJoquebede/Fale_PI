package br.edu.ifrn.sc.info;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import br.edu.ifrn.sc.info.R;

public class CadastrarPacienteActivity extends AppCompatActivity {

    private EditText edtNome, edtEmail, edtSenha, edtDataNasc;
    private Button btnCadastrar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_paciente);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtDataNasc = findViewById(R.id.edtDataNascimento);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> cadastrarNoSistema());
    }

    private void cadastrarNoSistema() {
        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();
        String dataNasc = edtDataNasc.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCadastrar.setEnabled(false);

        // 1. Criar o acesso no Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    salvarEmAmbasColecoes(uid, nome, email, dataNasc);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnCadastrar.setEnabled(true);
                });
    }

    private void salvarEmAmbasColecoes(String uid, String nome, String email, String dataNasc) {
        // Preparar os dados
        Map<String, Object> dados = new HashMap<>();
        dados.put("id", uid);
        dados.put("nome", nome);
        dados.put("email", email);
        dados.put("dataNasc", dataNasc);
        dados.put("tipo", false); // Indica que é um paciente

        // 2. Criar as tarefas de salvamento para as DUAS coleções ao mesmo tempo
        Task<Void> taskUsuarios = db.collection("usuarios").document(uid).set(dados);
        Task<Void> taskPacientes = db.collection("pacientes").document(uid).set(dados);

        // 3. Esperar que AMBAS terminem com sucesso
        Tasks.whenAll(taskUsuarios, taskPacientes)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Paciente cadastrado em ambas coleções!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao sincronizar bancos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    btnCadastrar.setEnabled(true);
                });
    }
}
