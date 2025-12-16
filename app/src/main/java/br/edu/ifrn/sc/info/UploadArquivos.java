package br.edu.ifrn.sc.info;

// AddItemActivity.java

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadArquivos extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_AUDIO_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 3;

    private String mThemeId; // ID do Tema passado via Intent
    private Uri mImageUri;
    private Uri mAudioUri;

    private EditText mPalavraEditText;
    private EditText mSilabicaEditText;
    private Button mAddItemButton;
    private TextView mImageStatus, mAudioStatus;
    private String blocoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_arquivos);
        blocoSelecionado = getIntent().getStringExtra("bloco");


        // Recebe o ID do tema da tela anterior
        mThemeId = getIntent().getStringExtra("THEME_ID");
        if (mThemeId == null) {
            Toast.makeText(this, "Erro: ID do Tema não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mPalavraEditText = findViewById(R.id.edit_palavra);
        mSilabicaEditText = findViewById(R.id.edit_silabica);
        mAddItemButton = findViewById(R.id.btn_add_item);
        mImageStatus = findViewById(R.id.status_image);
        mAudioStatus = findViewById(R.id.status_audio);

        findViewById(R.id.btn_select_image).setOnClickListener(v -> selectFile(PICK_IMAGE_REQUEST, "image/*"));
        findViewById(R.id.btn_record_audio).setOnClickListener(v -> checkPermissionAndSelectAudio());
        mAddItemButton.setOnClickListener(v -> validateAndUploadItem());
        mAddItemButton.setOnClickListener(v -> {
            salvarAtividade();
        });
    }
    private void salvarAtividade() {

        String palavra = mPalavraEditText.getText().toString().trim();
        String silabas = mSilabicaEditText.getText().toString().trim();

        if (palavra.isEmpty() || silabas.isEmpty()) {
            Toast.makeText(this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        finish();
    }

    private void selectFile(int requestCode, String type) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        startActivityForResult(intent, requestCode);
    }

    private void checkPermissionAndSelectAudio() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION);
        } else {
            // Se as permissões estiverem ok, abre o seletor de áudio (ou inicia a gravação)
            selectFile(PICK_AUDIO_REQUEST, "audio/*");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                mImageUri = data.getData();
                mImageStatus.setText("Imagem selecionada: " + mImageUri.getLastPathSegment());
            } else if (requestCode == PICK_AUDIO_REQUEST) {
                mAudioUri = data.getData();
                mAudioStatus.setText("Áudio selecionado: " + mAudioUri.getLastPathSegment());
            }
        }
    }

    private void validateAndUploadItem() {
        final String palavra = mPalavraEditText.getText().toString().trim();
        final String silabica = mSilabicaEditText.getText().toString().trim();

        if (palavra.isEmpty() || mImageUri == null || mAudioUri == null) {
            Toast.makeText(this, "Preencha a palavra e selecione Imagem e Áudio.", Toast.LENGTH_LONG).show();
            return;
        }

        mAddItemButton.setEnabled(false); // Desabilita o botão

        // 1. Upload da Imagem
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("activities/" + mThemeId)
                .child(UUID.randomUUID().toString() + "_img.jpg");
        UploadTask imageUploadTask = imageRef.putFile(mImageUri);

        // 2. Upload do Áudio
        StorageReference audioRef = FirebaseStorage.getInstance().getReference("activities/" + mThemeId)
                .child(UUID.randomUUID().toString() + "_audio.mp3");
        UploadTask audioUploadTask = audioRef.putFile(mAudioUri);

        // 3. Executa ambas as tarefas e espera o resultado
        Task<Uri> getImageUriTask = imageUploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) { throw task.getException(); }
            return imageRef.getDownloadUrl();
        });

        Task<Uri> getAudioUriTask = audioUploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) { throw task.getException(); }
            return audioRef.getDownloadUrl();
        });

        // 4. Combina as tarefas de download URL
        Tasks.whenAllSuccess(getImageUriTask, getAudioUriTask)
                .addOnSuccessListener(results -> {
                    // Resultados[0] é a URL da Imagem, Resultados[1] é a URL do Áudio
                    String imageUrl = results.get(0).toString();
                    String audioUrl = results.get(1).toString();

                    // 5. Salva os metadados no Firestore
                    saveItemToFirestore(palavra, silabica, imageUrl, audioUrl);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UploadArquivos.this, "Falha no upload de um ou ambos arquivos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    mAddItemButton.setEnabled(true);
                });
    }


    // Método para salvar o item (metadados) no array 'items' do documento do Tema no Firestore
    private void saveItemToFirestore(String palavra, String silabica, String imageUrl, String audioUrl) {
        DocumentReference themeRef = FirebaseFirestore.getInstance().collection("themes").document(mThemeId);

        Map<String, Object> newItem = new HashMap<>();
        newItem.put("palavra", palavra);
        newItem.put("silabica", silabica);
        newItem.put("imageUrl", imageUrl);
        newItem.put("audioUrl", audioUrl);

        // Usa FieldValue.arrayUnion para adicionar o novo mapa ao array 'items'
        themeRef.update("items", FieldValue.arrayUnion(newItem))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UploadArquivos.this, "Item " + palavra + " adicionado com sucesso", Toast.LENGTH_SHORT).show();
                    // Limpa o formulário
                    mPalavraEditText.setText("");
                    mSilabicaEditText.setText("");
                    mImageUri = null;
                    mAudioUri = null;
                    mImageStatus.setText("Nenhuma imagem selecionada.");
                    mAudioStatus.setText("Nenhum áudio selecionado/gravado.");
                    mAddItemButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UploadArquivos.this, "Falha ao salvar item no Firestore.", Toast.LENGTH_SHORT).show();
                    mAddItemButton.setEnabled(true);
                });

    }
}
