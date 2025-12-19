package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

public class DetalhePacienteActivity extends AppCompatActivity {

    private String idPaciente;
    private TextView tvNomePaciente;
    private RecyclerView rvAtividades;
    private Button btnEnviarAtividade;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_paciente);

        idPaciente = getIntent().getStringExtra("idPaciente");

        tvNomePaciente = findViewById(R.id.tvNomePaciente);
        rvAtividades = findViewById(R.id.rvAtividades);
        btnEnviarAtividade = findViewById(R.id.btnEnviarAtividade);

        db = FirebaseFirestore.getInstance();

        btnEnviarAtividade.setOnClickListener(v -> {
            Intent intent = new Intent(this, BlocosActivity.class);
            intent.putExtra("idPaciente", idPaciente);
            startActivity(intent);
        });

        // aqui carrega dados do paciente e atividades
    }
}

