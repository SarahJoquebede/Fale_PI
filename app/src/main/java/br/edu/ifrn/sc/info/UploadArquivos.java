package br.edu.ifrn.sc.info;

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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import br.edu.ifrn.sc.info.utils.FirebaseUtils;


public class UploadArquivos extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_AUDIO_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 3;

    private String mThemeId;
    private String mPacienteId; // Variável para o paciente específico

    private Uri mImageUri;
    private Uri mAudioUri;

    private EditText mPalavraEditText;
    private EditText mSilabicaEditText;
    private Button mAddItemButton;
    private TextView mImageStatus, mAudioStatus;

    private FirebaseUtils firebaseUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_arquivos);

        firebaseUtils = new FirebaseUtils();

        // Recupera o ID do tema e o ID do paciente vindos do Dashboard
        mThemeId = getIntent().getStringExtra("THEME_ID");

        if (mThemeId == null) {
            mThemeId = "animais";
        }

        initViews();
    }

    private void initViews() {
        mPalavraEditText = findViewById(R.id.edit_palavra);
        mSilabicaEditText = findViewById(R.id.edit_silabica);
        mAddItemButton = findViewById(R.id.btn_add_item);
        mImageStatus = findViewById(R.id.status_image);
        mAudioStatus = findViewById(R.id.status_audio);

        findViewById(R.id.btn_select_image).setOnClickListener(v -> selectFile(PICK_IMAGE_REQUEST, "image/*"));
        findViewById(R.id.btn_record_audio).setOnClickListener(v -> checkPermissionAndSelectAudio());
        mAddItemButton.setOnClickListener(v -> validateAndUpload());
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
            selectFile(PICK_AUDIO_REQUEST, "audio/*");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                mImageUri = data.getData();
                mImageStatus.setText("Imagem selecionada!");
            } else if (requestCode == PICK_AUDIO_REQUEST) {
                mAudioUri = data.getData();
                mAudioStatus.setText("Áudio selecionado!");
            }
        }
    }

    private void validateAndUpload() {
        String palavra = mPalavraEditText.getText().toString().trim();
        String silabica = mSilabicaEditText.getText().toString().trim();

        if (palavra.isEmpty() || mImageUri == null || mAudioUri == null) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAddItemButton.setEnabled(false);

        mAddItemButton.setEnabled(false);
        Toast.makeText(this, "Fazendo upload...", Toast.LENGTH_SHORT).show();

        // 1. Faz o upload para a biblioteca geral (FirebaseUtils)
        firebaseUtils.uploadActivityItem(mThemeId, palavra, silabica, mImageUri, mAudioUri, new FirebaseUtils.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl, String audioUrl) {
                // Se salvou no Geral, agora verifica se manda para o Paciente
                if (mPacienteId != null && !mPacienteId.isEmpty()) {
                    salvarNaPastaDoPaciente(palavra, silabica, imageUrl, audioUrl);
                } else {
                    Toast.makeText(UploadArquivos.this, "Salvo na biblioteca geral!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UploadArquivos.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
                mAddItemButton.setEnabled(true);
            }
        });
    }

    private void salvarNaPastaDoPaciente(String palavra, String silabica, String imageUrl, String audioUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> dados = new HashMap<>();
        dados.put("palavra", palavra);
        dados.put("silabica", silabica);
        dados.put("imagemUrl", imageUrl);
        dados.put("audioUrl", audioUrl);

        // .set(..., SetOptions.merge()) resolve o erro de NOT_FOUND criando as pastas automaticamente
        db.collection("pacientes")
                .document(mPacienteId)
                .collection("blocosRecebidos")
                .document("plosivizacao_" + mThemeId)
                .collection("atividades")
                .document(palavra)
                .set(dados, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Salvo no Geral e no Paciente!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao vincular paciente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}