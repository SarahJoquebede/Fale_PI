package br.edu.ifrn.sc.info;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class ReviewActivity extends AppCompatActivity{
    private RatingBar rbNota; //ANTES ERA UM BOTAO ESSE
    private EditText etComentario; //ESSE TBM
    private String audioUrl, audioId;

    private MediaPlayer player;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //Achou o botao
        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        rbNota = findViewById(R.id.rbNota);
        etComentario = findViewById(R.id.etComentario);

        //Pegando as infos da tela anterior
        audioUrl = getIntent().getStringExtra("audioUrl");
        audioId = getIntent().getStringExtra("audioId");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnPlay.setOnClickListener(v -> playAudio());
        btnSalvar.setOnClickListener(v -> salvarAvaliacao());
    }

    private void playAudio() {
        try {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
            player = new MediaPlayer();
            player.setDataSource(audioUrl);
            player.prepare();
            player.start();
            Toast.makeText(this, "Reproduzindo...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarAvaliacao() {
        // 1. Captura os dados da tela
        float estrelasProgresso = rbNota.getRating(); // Estrelas para o progresso do paciente
        String observacaoClinica = etComentario.getText().toString(); // Comentário guardado para o fono

        if (observacaoClinica.isEmpty()) {
            Toast.makeText(this, "Por favor, insira a observação clínica.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Organiza os dados para o Firestore
        Map<String, Object> avaliacao = new HashMap<>();
        avaliacao.put("pacienteId", user.getUid()); // ID do paciente (visto que ele está logado)
        avaliacao.put("notaEstrelas", estrelasProgresso);
        avaliacao.put("comentarioFono", observacaoClinica);
        avaliacao.put("audioRelacionado", audioId);
        avaliacao.put("dataAvaliacao", new Date());

        // 3. Salva em uma coleção de 'historico_atividades'
        // Assim o fonoaudiólogo pode consultar depois filtrando pelo ID do paciente
        db.collection("historico_atividades").add(avaliacao)
                .addOnSuccessListener(documentReference -> {

                    // 4. Opcional: Atualizar a última nota no cadastro do paciente para o fono ver na lista
                    db.collection("pacientes").document(user.getUid())
                            .update("ultimoProgresso", estrelasProgresso);

                    Toast.makeText(this, "Avaliação clínica salva com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
