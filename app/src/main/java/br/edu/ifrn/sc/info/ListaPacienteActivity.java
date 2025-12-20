package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.sc.info.dominio.Paciente;

public class ListaPacienteActivity extends AppCompatActivity {

    private RecyclerView rvPacientes;
    private FloatingActionButton fabAddPaciente;

    private FirebaseFirestore db;
    private List<Paciente> lista = new ArrayList<>();
    private PacienteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listapacientes);

        rvPacientes = findViewById(R.id.rvPacientes);
        fabAddPaciente = findViewById(R.id.fabAddPaciente);

        db = FirebaseFirestore.getInstance();

        adapter = new PacienteAdapter(lista, this);
        rvPacientes.setLayoutManager(new LinearLayoutManager(this));
        rvPacientes.setAdapter(adapter);

        fabAddPaciente.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastrarPacienteActivity.class));
        });

        carregarPacientes();
    }

    private void carregarPacientes() {
        db.collection("pacientes")
                .orderBy("nome")
                .addSnapshotListener((value, error) -> {
                    if (value == null) return;

                    lista.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Paciente paciente = doc.toObject(Paciente.class);
                        paciente.setId(doc.getId()); // 🔥 AQUI INTEGRA O ID
                        lista.add(paciente);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

}

