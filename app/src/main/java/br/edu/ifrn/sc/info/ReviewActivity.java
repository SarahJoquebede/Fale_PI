package br.edu.ifrn.sc.info;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class ReviewActivity extends AppCompatActivity{
    private SeekBar sbNota; //ANTES ERA UM BOTAO ESSE
    private EditText etComentario; //ESSE TBM
    private String audioUrl, audioId;

    private MediaPlayer player;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnSalvar = findViewById(R.id.btnSalvar);
        sbNota = findViewById(R.id.sbNota);
        etComentario = findViewById(R.id.etComentario);

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
        int nota = sbNota.getProgress();
        String comentario = etComentario.getText().toString();

        Map<String, Object> aval = new HashMap<>();
        aval.put("avaliadorId", user.getUid());
        aval.put("nota", nota);
        aval.put("comentario", comentario);

        db.collection("audios").document(audioId)
                .collection("avaliacoes").add(aval)
                .addOnSuccessListener(r -> Toast.makeText(this, "Avaliação salva!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
