package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardTeste extends Fragment {

    private Button btnEnviarAnimais;
    private Button btnAdicionarAtividade;

    private String pacienteId;
    private String pacienteNome;

    public DashboardTeste() {
        // Construtor vazio obrigatório
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Verifique se o nome do layout abaixo está correto (fragment_dashboard_teste.xml)
        return inflater.inflate(R.layout.fragment_dashboard_teste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tenta buscar o ID do paciente na MenuTeste
        if (getActivity() instanceof MenuTeste) {
            pacienteId = ((MenuTeste) getActivity()).getPacienteSelecionadoId();
        }

        // 2. Inicializa os botões
        btnAdicionarAtividade = view.findViewById(R.id.btnAdicionarAtividade);
        btnEnviarAnimais = view.findViewById(R.id.btnEnviar);

        // 3. Configura o botão de Upload (Adicionar)
        if (btnAdicionarAtividade != null) {
            btnAdicionarAtividade.setOnClickListener(v -> {
                // Log para você ver no Logcat se o clique funcionou
                android.util.Log.d("DEBUG_APP", "Clique no botão Adicionar");

                Intent intent = new Intent(getActivity(), UploadArquivos.class);
                intent.putExtra("THEME_ID", "animais");

                // Passa o ID se ele existir, mas abre a tela de qualquer jeito
                if (pacienteId != null) {
                    intent.putExtra("PACIENTE_ID", pacienteId);
                }

                startActivity(intent);
            });
        } else {
            android.util.Log.e("DEBUG_APP", "Erro: Botão btnAdicionarAtividade não encontrado no XML!");
        }

        // 4. Configura o botão de Enviar Bloco
        if (btnEnviarAnimais != null) {
            btnEnviarAnimais.setOnClickListener(v -> {
                if (pacienteId != null) {
                    enviarBlocoParaPaciente("animais");
                } else {
                    Toast.makeText(getContext(), "Selecione um paciente na lista primeiro!", Toast.LENGTH_SHORT).show();
                }
            });

        }

        // 4. Configura o clique para Enviar bloco existente
        if (btnEnviarAnimais != null) {
            btnEnviarAnimais.setOnClickListener(v -> {
                if (pacienteId != null) {
                    enviarBlocoParaPaciente("animais");
                } else {
                    Toast.makeText(getContext(), "Nenhum paciente selecionado!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void enviarBlocoParaPaciente(String bloco) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referência da biblioteca de atividades (sua fonte)
        CollectionReference atividadesRef = db.collection("blocos")
                .document("plosivizacao")
                .collection("categorias")
                .document(bloco)
                .collection("atividades");

        atividadesRef.get().addOnSuccessListener(querySnapshot -> {
            if (querySnapshot.isEmpty()) {
                Toast.makeText(getContext(), "Não há atividades para enviar.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Loop para copiar cada atividade para a pasta do paciente
            for (QueryDocumentSnapshot doc : querySnapshot) {
                db.collection("pacientes")
                        .document(pacienteId)
                        .collection("blocosRecebidos")
                        .document("plosivizacao_" + bloco)
                        .collection("atividades")
                        .document(doc.getId())
                        .set(doc.getData());
            }

            String msg = "Bloco enviado para " + (pacienteNome != null ? pacienteNome : "o paciente");
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Erro ao buscar atividades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}