package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class BlocosActivity extends AppCompatActivity {

    CardView cardAnimais;
    Button btnEnviarAnimais;
    Button btnAdicionarAtividade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocos);



        cardAnimais = findViewById(R.id.cardAnimais);
        btnEnviarAnimais = findViewById(R.id.btnEnviarAnimais);
        btnAdicionarAtividade = findViewById(R.id.btnAdicionarAtividade);


        // Abrir tela de upload

        btnAdicionarAtividade.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BlocosActivity.this,
                    UploadArquivos.class
            );
            intent.putExtra("bloco", "animais");
            startActivity(intent);
        });
        cardAnimais.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BlocosActivity.this,
                    UploadArquivos.class
            );
            intent.putExtra("bloco", "Animais");
            startActivity(intent);
        });

        // Enviar bloco inteiro para o paciente
        btnEnviarAnimais.setOnClickListener(v -> {
            enviarBlocoParaPaciente("Animais");
        });
    }

    private void enviarBlocoParaPaciente(String bloco) {
        // Aqui você vai copiar todas as atividades
        // desse bloco para o paciente no Firestore, JÁ CONFIGUREI O FIRESTORE


            String pacienteId = "paciente123"; // depois você vai selecionar isso

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CollectionReference atividadesRef =
                    db.collection("blocos")
                            .document("plosivizacao")
                            .collection("categorias")
                            .document(bloco)
                            .collection("atividades");

            atividadesRef.get().addOnSuccessListener(querySnapshot -> {

                if (querySnapshot.isEmpty()) {
                    Toast.makeText(this,
                            "Não há atividades nesse bloco",
                            Toast.LENGTH_SHORT).show();
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

                Toast.makeText(this,
                        "Bloco enviado para o paciente!",
                        Toast.LENGTH_SHORT).show();
            });
        }


    }

