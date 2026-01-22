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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardTeste extends Fragment {

    private CardView cardAnimais;
    private Button btnEnviarAnimais;
    private Button btnAdicionarAtividade;

    public DashboardTeste() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_dashboard_teste, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardAnimais = view.findViewById(R.id.cardAnimais);
        btnEnviarAnimais = view.findViewById(R.id.btnEnviarAnimais);
        btnAdicionarAtividade = view.findViewById(R.id.btnAdicionarAtividade);

        btnAdicionarAtividade.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UploadArquivos.class);
            intent.putExtra("bloco", "animais");
            startActivity(intent);
        });

        cardAnimais.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UploadArquivos.class);
            intent.putExtra("bloco", "Animais");
            startActivity(intent);
        });

        btnEnviarAnimais.setOnClickListener(v -> {
            enviarBlocoParaPaciente("Animais");
        });
    }

    private void enviarBlocoParaPaciente(String bloco) {

        String pacienteId = "paciente123"; // depois você vai tornar dinâmico
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference atividadesRef =
                db.collection("blocos")
                        .document("plosivizacao")
                        .collection("categorias")
                        .document(bloco)
                        .collection("atividades");

        atividadesRef.get().addOnSuccessListener(querySnapshot -> {

            if (querySnapshot.isEmpty()) {
                Toast.makeText(
                        getContext(),
                        "Não há atividades nesse bloco",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            for (QueryDocumentSnapshot doc : querySnapshot) {
                db.collection("pacientes")
                        .document(pacienteId)
                        .collection("blocosRecebidos")
                        .document("plosivizacao_" + bloco)
                        .collection("atividades")
                        .document(doc.getId())
                        .set(doc.getData());
            }

            Toast.makeText(
                    getContext(),
                    "Bloco enviado para o paciente!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
