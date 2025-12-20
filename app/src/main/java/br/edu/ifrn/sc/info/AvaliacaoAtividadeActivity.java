package br.edu.ifrn.sc.info;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AvaliacaoAtividadeActivity extends AppCompatActivity {

    private EditText edtFeedback;
    private RatingBar ratingBar;
    private String idPaciente, idAtividade;
    private ImageButton btnPlayAudio;
    private String arquivoUrl;
    private MediaPlayer mediaPlayer;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao_atividade);

        edtFeedback = findViewById(R.id.edtFeedback);
        ratingBar = findViewById(R.id.ratingBar);

        idPaciente = getIntent().getStringExtra("idPaciente");
        idAtividade = getIntent().getStringExtra("idAtividade");

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarAvaliacao());

        db.collection("pacientes")
                .document(idPaciente)
                .collection("atividades")
                .document(idAtividade)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        arquivoUrl = doc.getString("arquivoUrl");
                    }
                });
        ImageButton btnPlay = findViewById(R.id.btnPlayAudio);

        btnPlay.setOnClickListener(v -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(arquivoUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void salvarAvaliacao() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("feedbackFono", edtFeedback.getText().toString());
        dados.put("avaliacao", (int) ratingBar.getRating());

        db.collection("pacientes")
                .document(idPaciente)
                .collection("atividades")
                .document(idAtividade)
                .update(dados)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Avaliação salva", Toast.LENGTH_SHORT).show()
                );
    }


}
