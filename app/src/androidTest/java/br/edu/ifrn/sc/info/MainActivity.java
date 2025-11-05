package br.edu.ifrn.sc.info;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 100;
    private MediaRecorder recorder;
    private File audioFile;
    private TextView tvStatus;
    private Button btnStart, btnStop;
    private ProgressBar progressUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        progressUpload = findViewById(R.id.progressUpload);


        if (!hasPermissions()) {
            requestPermissions();
        }

        btnStart.setOnClickListener(v -> startRecording());
        btnStop.setOnClickListener(v -> stopRecording());
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CODE);
    }

    private void startRecording() {
        try {
            audioFile = new File(getExternalFilesDir(null), "gravacao_" + System.currentTimeMillis() + ".mp4");

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(audioFile.getAbsolutePath());

            recorder.prepare();
            recorder.start();

            tvStatus.setText("Gravando...");
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao iniciar gravação", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;

            tvStatus.setText("Gravação concluída!");
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);

            uploadToFirebase(audioFile);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao parar gravação", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(File file) {
        progressUpload.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setText("Enviando áudio...");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audios/" + file.getName());

        Uri fileUri = Uri.fromFile(file);
        UploadTask uploadTask = audioRef.putFile(fileUri);

        uploadTask.addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            tvStatus.setText(String.format("Enviando: %.0f%%", progress));
        }).addOnSuccessListener(taskSnapshot -> {
            progressUpload.setVisibility(ProgressBar.GONE);
            tvStatus.setText("Upload concluído!");
            Toast.makeText(this, "Áudio enviado com sucesso!", Toast.LENGTH_SHORT).show();
            file.delete(); // opcional: apagar após envio
        }).addOnFailureListener(e -> {
            progressUpload.setVisibility(ProgressBar.GONE);
            tvStatus.setText("Falha no upload");
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}

