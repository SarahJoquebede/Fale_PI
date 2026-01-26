package br.edu.ifrn.sc.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListaAtividadesActivity extends AppCompatActivity {
    private RecyclerView rv;
    private AtividadeAdapter adapter;
    private List<Atividade> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_atividades); // Crie um XML com um RecyclerView simples

        rv = findViewById(R.id.recyclerViewAtividades);
        rv.setLayoutManager(new LinearLayoutManager(this));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("pacientes")
                .document(uid)
                .collection("blocosRecebidos")
                .document("plosivizacao_animais")
                .collection("atividades")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        lista.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            lista.add(doc.toObject(Atividade.class));
                        }
                        adapter = new AtividadeAdapter(lista, this);
                        rv.setAdapter(adapter);
                    }
                });
    }
}