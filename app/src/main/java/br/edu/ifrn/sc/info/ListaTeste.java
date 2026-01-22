package br.edu.ifrn.sc.info;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifrn.sc.info.dominio.Paciente;


public class ListaTeste extends Fragment {


    private RecyclerView rvPacientes;
    private PacienteAdapter pacienteAdapter;
    private List<Paciente> listaDePacientes;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FloatingActionButton btnCadastrar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_lista_teste, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        rvPacientes = view.findViewById(R.id.rvPacientes);
        progressBar = view.findViewById(R.id.progressBar);
        btnCadastrar = view.findViewById(R.id.fabAddPaciente);

        rvPacientes.setHasFixedSize(true);
        rvPacientes.setLayoutManager(new LinearLayoutManager(getContext())); // Usa getContext() para o contexto do fragmento

        db = FirebaseFirestore.getInstance();
        listaDePacientes = new ArrayList<>();

        // Cria e configura o Adapter que vai popular o RecyclerView
        pacienteAdapter = new PacienteAdapter(listaDePacientes, getContext()); // Passa o contexto do fragmento
        rvPacientes.setAdapter(pacienteAdapter);


        btnCadastrar.setOnClickListener(v -> {

            Intent intent = new Intent(getActivity(), CadastrarPacienteActivity.class); // Usa getActivity() para iniciar uma Activity
            startActivity(intent);
        });


        carregarPacientesDoFirestore();
    }

    private void carregarPacientesDoFirestore() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        db.collection("pacientes")
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (task.isSuccessful() && task.getResult() != null) {
                        listaDePacientes.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Converte cada documento do Firestore em um objeto Paciente
                            Paciente paciente = document.toObject(Paciente.class);
                            // Define o ID do documento no objeto, que é crucial para editar/excluir paciente
                            paciente.setId(document.getId());                      // Adiciona o paciente na lista
                            listaDePacientes.add(paciente);
                        }

                        pacienteAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Erro ao carregar pacientes.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}







