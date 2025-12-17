package br.edu.ifrn.sc.info;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private Button btnStart;
    private Button btnStop;
    private Button btnList;
    private TextView tvStatus;
    private ProgressBar progressUpload;

    private MediaRecorder recorder;
    private String filePath;
    private StorageReference storage;

    private FirebaseFirestore db;
    private FirebaseUser user;

    private CardView cvPendentesDetalhes;
    private Button botaoPontos;
    private RecyclerView rvPendentes;
    private ImageView imgAtividade;
    private Button btnPlayAtividade;

    private String imageUrl;
    private String audioUrl;

    private MediaPlayer mediaPlayer;

    private static final int PERM_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnList = findViewById(R.id.btnList);
        tvStatus = findViewById(R.id.tvStatus);
        progressUpload = findViewById(R.id.progressUpload);
        imgAtividade = findViewById(R.id.imgAtividade);
        btnPlayAtividade = findViewById(R.id.btnPlayAtividade);


        // Inicializa o Storage apontando diretamente para o bucket do seu projeto Firebase
        storage = FirebaseStorage
                .getInstance("gs://projetinho-ac630.firebasestorage.app") // <-- substitua pelo nome do SEU bucket
                .getReference();

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        /*
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }*/

        checkPermissions();

        btnStart.setOnClickListener(v -> gravar());
        btnStop.setOnClickListener(v -> pararDeGravar());
        btnList.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListAudioActivity.class));
        });
        db.collection("blocos")
                .document("plosivizacao")
                .collection("categorias")
                .document("animais")
                .collection("atividades")
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);

                        imageUrl = doc.getString("imageUrl");
                        audioUrl = doc.getString("audioUrl");

                        mostrarImagemAtividade();
                        prepararAudioAtividade();
                    }
                });

    }
    private void mostrarImagemAtividade() {

        if (imageUrl == null) return;

        imgAtividade.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(imageUrl)
                .into(imgAtividade);
    }
    private void prepararAudioAtividade() {

        if (audioUrl == null) return;

        btnPlayAtividade.setVisibility(View.VISIBLE);

        btnPlayAtividade.setOnClickListener(v -> {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();

            } catch (Exception e) {
                Toast.makeText(this,
                        "Erro ao reproduzir áudio da atividade",
                        Toast.LENGTH_SHORT).show();
            }
        });
    } // Parte do código relacionada pela busca no Firestore da atividade criada na tela de upload
    // (imagem e áudio salvos anteriormente pelo fono)
    // Carrega a atividade (imagem e áudio) que foi salva no Firestore


    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERM_CODE);
                break;
            }
        }
    }

    private void gravar() {
        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            assert dir != null;
            filePath = dir.getAbsolutePath() + "/audio_" + timeStamp + ".3gp";

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(filePath);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.prepare();
            recorder.start();

            tvStatus.setText("Gravando...");
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnList.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERRO", e.getMessage());
            Toast.makeText(this, "Erro ao gravar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void pararDeGravar() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;
            tvStatus.setText("Gravação finalizada. Enviando...");
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnList.setEnabled(true);

            uploadToFirebase(); //COLOCAR O NOME DO ARQUIVO PARA SALVAR
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao parar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase() {
        progressUpload.setVisibility(View.VISIBLE);
        File audioFile = new File(filePath);

        if (!audioFile.exists()) {
            tvStatus.setText("Erro: Arquivo local não encontrado!");
            progressUpload.setVisibility(View.GONE);
            return;
        }

        Uri fileUri = Uri.fromFile(audioFile);
        String fileName = fileUri.getLastPathSegment();

        StorageReference ref = storage.child("audios/" + fileName);

        ref.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Agora usamos o próprio snapshot pra pegar o link
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        saveMetadata(uri.toString());
                        progressUpload.setVisibility(View.GONE);
                        tvStatus.setText("Áudio enviado!");
                    });
                })
                .addOnFailureListener(e -> {
                    progressUpload.setVisibility(View.GONE);
                    tvStatus.setText("Erro no upload: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    private void saveMetadata(String downloadUrl) {
        Map<String, Object> audio = new HashMap<>();
        audio.put("autorId", user.getUid());
        audio.put("autorEmail", user.getEmail());
        audio.put("arquivoUrl", downloadUrl);
        audio.put("dataEnvio", new Date());

        db.collection("audios").add(audio)
                .addOnSuccessListener(doc -> Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar metadados", Toast.LENGTH_LONG).show());
    }

}

