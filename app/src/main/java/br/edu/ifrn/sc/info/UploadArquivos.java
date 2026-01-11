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
import br.edu.ifrn.sc.info.utils.FirebaseUtils;


public class UploadArquivos extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_AUDIO_REQUEST = 2;
    private static final int REQUEST_PERMISSION = 3;

    // Variável que identifica a qual tema essa atividade pertence
    private String mThemeId;

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

        // 1. Tenta recuperar o ID do tema vindo da tela anterior
        mThemeId = getIntent().getStringExtra("THEME_ID");

        // 2. Lógica de segurança/teste:
        if (mThemeId == null) {
            // Se for nulo, definimos um ID padrão para que você consiga TESTAR a tela agora
            mThemeId = "tema_geral_teste";
            Toast.makeText(this, "Aviso: Usando ID de teste (tema_geral_teste)", Toast.LENGTH_SHORT).show();
        }

        initViews();
    }

    private void initViews() {
        mPalavraEditText = findViewById(R.id.edit_palavra);
        mSilabicaEditText = findViewById(R.id.edit_silabica);
        mAddItemButton = findViewById(R.id.btn_add_item);
        mImageStatus = findViewById(R.id.status_image);
        mAudioStatus = findViewById(R.id.status_audio);

        // Seleção de Imagem
        findViewById(R.id.btn_select_image).setOnClickListener(v -> selectFile(PICK_IMAGE_REQUEST, "image/*"));

        // Seleção de Áudio com verificação de permissão
        findViewById(R.id.btn_record_audio).setOnClickListener(v -> checkPermissionAndSelectAudio());

        // Botão Final de Envio
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
                mImageStatus.setText("Imagem selecionada com sucesso!");
            } else if (requestCode == PICK_AUDIO_REQUEST) {
                mAudioUri = data.getData();
                mAudioStatus.setText("Áudio selecionado com sucesso!");
            }
        }
    }

    private void validateAndUpload() {
        String palavra = mPalavraEditText.getText().toString().trim();
        String silabica = mSilabicaEditText.getText().toString().trim();

        if (palavra.isEmpty() || mImageUri == null || mAudioUri == null) {
            Toast.makeText(this, "Preencha a palavra e selecione os arquivos primeiro.", Toast.LENGTH_LONG).show();
            return;
        }

        mAddItemButton.setEnabled(false); // Evita cliques duplos durante o upload
        Toast.makeText(this, "Fazendo upload... aguarde.", Toast.LENGTH_SHORT).show();

        // Enviando para o Firebase usando o mThemeId (ID do Tema)
        firebaseUtils.uploadActivityItem(mThemeId, palavra, silabica, mImageUri, mAudioUri, new FirebaseUtils.UploadCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UploadArquivos.this, "Atividade salva com sucesso!", Toast.LENGTH_SHORT).show();
                resetForm();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UploadArquivos.this, "Falha: " + errorMessage, Toast.LENGTH_LONG).show();
                mAddItemButton.setEnabled(true);
            }
        });
    }

    private void resetForm() {
        mPalavraEditText.setText("");
        mSilabicaEditText.setText("");
        mImageUri = null;
        mAudioUri = null;
        mImageStatus.setText("Nenhuma imagem selecionada.");
        mAudioStatus.setText("Nenhum áudio selecionado.");
        mAddItemButton.setEnabled(true);
    }
}
