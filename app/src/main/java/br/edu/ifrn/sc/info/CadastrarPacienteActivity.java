package br.edu.ifrn.sc.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastrarPacienteActivity extends AppCompatActivity {

    private EditText edtNome, edtDataNascimento, edtEmail, edtSenha;
    private Button btnCadastrar;
        private ImageButton ibtnAtualizar, ibtnExcluir;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String idPaciente; // null = cadastro | preenchido = edição


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_paciente);

        edtNome = findViewById(R.id.edtNome);
        edtDataNascimento = findViewById(R.id.edtDataNascimento);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);

        btnCadastrar = findViewById(R.id.btnCadastrar);
        ibtnAtualizar = findViewById(R.id.ibtnAtualizar);
        ibtnExcluir = findViewById(R.id.ibtnExcluir);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔎 Verifica se veio um paciente para edição
        idPaciente = getIntent().getStringExtra("idPaciente");

        if (idPaciente != null) {
            carregarDadosPaciente();
            btnCadastrar.setVisibility(View.GONE);
            ibtnAtualizar.setVisibility(View.VISIBLE);
            ibtnExcluir.setVisibility(View.VISIBLE);
        }

        btnCadastrar.setOnClickListener(v -> cadastrarPaciente());
        ibtnAtualizar.setOnClickListener(v -> atualizarPaciente());
        ibtnExcluir.setOnClickListener(v -> excluirPaciente());

    }

    // 🟦 CREATE
    private void cadastrarPaciente() {

        String nome = edtNome.getText().toString();
        String nascimento = edtDataNascimento.getText().toString();
        String email = edtEmail.getText().toString();
        String senha = edtSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> {
                    String uid = result.getUser().getUid();

                    Map<String, Object> paciente = new HashMap<>();
                    paciente.put("nome", nome);
                    paciente.put("dataNascimento", nascimento);
                    paciente.put("email", email);

                    db.collection("pacientes")
                            .document(uid)
                            .set(paciente)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Paciente cadastrado", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                });
    }

    // 🟦 READ
    private void carregarDadosPaciente() {
        db.collection("pacientes")
                .document(idPaciente)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        edtNome.setText(doc.getString("nome"));
                        edtDataNascimento.setText(doc.getString("dataNascimento"));
                        edtEmail.setText(doc.getString("email"));

                        // senha não é recuperável
                        edtSenha.setVisibility(View.GONE);
                    }
                });
    }

    // 🟦 UPDATE
    private void atualizarPaciente() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", edtNome.getText().toString());
        dados.put("dataNascimento", edtDataNascimento.getText().toString());

        db.collection("pacientes")
                .document(idPaciente)
                .update(dados)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Dados atualizados", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // 🟦 DELETE
    private void excluirPaciente() {
        db.collection("pacientes")
                .document(idPaciente)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Paciente excluído", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

}
