package br.edu.ifrn.sc.info;

import android.os.Bundle;import android.view.View;
import android.widget.ProgressBar; // Para feedback visual de carregamento
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.sc.info.dominio.Paciente;

public class ListaPacienteActivity extends AppCompatActivity {

    private RecyclerView rvPacientes;
    private PacienteAdapter pacienteAdapter;
    private List<Paciente> listaDePacientes;
    private FirebaseFirestore db;
    private ProgressBar progressBar; // Opcional, mas recomendado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listapacientes);

        // 1. Inicializa os componentes
        rvPacientes = findViewById(R.id.rvPacientes); // Garanta que este ID está no seu XML
        progressBar = findViewById(R.id.progressBar); // Garanta que este ID está no seu XML

        // 2. Configura o RecyclerView
        rvPacientes.setHasFixedSize(true);
        rvPacientes.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializa o Firestore e a lista
        db = FirebaseFirestore.getInstance();
        listaDePacientes = new ArrayList<>();

        // 4. Configura o Adapter (que agora tem as lógicas de avaliar e excluir)
        pacienteAdapter = new PacienteAdapter(listaDePacientes, this);
        rvPacientes.setAdapter(pacienteAdapter);

        // 5. Chama a função para buscar os dados
        carregarPacientesDoFirestore();
    }

    private void carregarPacientesDoFirestore() {
        // Mostra a barra de progresso enquanto os dados são carregados
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // AQUI ESTÁ A CORREÇÃO PRINCIPAL: Usando a coleção "pacientes"
        db.collection("pacientes") // <<-- BUSCANDO NA COLEÇÃO CORRETA
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // Esconde a barra de progresso após a conclusão
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }

                        if (task.isSuccessful()) {
                            // Limpa a lista antiga para evitar duplicatas ao recarregar
                            listaDePacientes.clear();

                            // Itera sobre cada documento retornado
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Converte o documento em um objeto Paciente
                                Paciente paciente = document.toObject(Paciente.class);
                                // IMPORTANTE: Define o ID do documento no objeto
                                paciente.setId(document.getId());
                                // Adiciona o paciente à lista
                                listaDePacientes.add(paciente);
                            }

                            // Notifica o adapter que os dados mudaram, para que ele atualize a tela
                            pacienteAdapter.notifyDataSetChanged();

                        } else {
                            // Se a tarefa falhar, mostra um erro
                            Toast.makeText(ListaPacienteActivity.this, "Erro ao carregar pacientes: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
