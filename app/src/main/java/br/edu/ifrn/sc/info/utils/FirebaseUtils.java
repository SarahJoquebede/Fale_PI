package br.edu.ifrn.sc.info.utils;

import android.net.Uri;
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

public class FirebaseUtils {

    // Interface para comunicar o resultado de volta para a Activity
    public interface UploadCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    /**
     * Realiza o upload da imagem e do áudio e, se bem-sucedido, salva os dados no Firestore.
     */
    public void uploadActivityItem(String themeId, String palavra, String silabica,
                                   Uri imageUri, Uri audioUri, UploadCallback callback) {

        // 1. Preparar referências do Storage (Pastas no Firebase)
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("activities/" + themeId);

        // Gera nomes únicos para os arquivos
        StorageReference imageRef = storageRef.child(UUID.randomUUID().toString() + "_img.jpg");
        StorageReference audioRef = storageRef.child(UUID.randomUUID().toString() + "_audio.mp3");

        // 2. Iniciar tarefas de Upload
        UploadTask uploadImgTask = imageRef.putFile(imageUri);
        UploadTask uploadAudioTask = audioRef.putFile(audioUri);

        // 3. Tarefa para pegar a URL da Imagem após o upload
        Task<Uri> urlImgTask = uploadImgTask.continueWithTask(task -> {
            if (!task.isSuccessful()) throw task.getException();
            return imageRef.getDownloadUrl();
        });

        // 4. Tarefa para pegar a URL do Áudio após o upload
        Task<Uri> urlAudioTask = uploadAudioTask.continueWithTask(task -> {
            if (!task.isSuccessful()) throw task.getException();
            return audioRef.getDownloadUrl();
        });

        // 5. Esperar as duas tarefas (Imagem e Áudio) terminarem com sucesso
        Tasks.whenAllSuccess(urlImgTask, urlAudioTask)
                .addOnSuccessListener(results -> {
                    // results.get(0) é o resultado da primeira tarefa (Imagem)
                    // results.get(1) é o resultado da segunda tarefa (Áudio)
                    String finalImageUrl = results.get(0).toString();
                    String finalAudioUrl = results.get(1).toString();

                    // 6. Tudo pronto, agora salvamos os textos e links no banco
                    saveToFirestore(themeId, palavra, silabica, finalImageUrl, finalAudioUrl, callback);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure("Erro ao fazer upload dos arquivos: " + e.getMessage());
                });
    }

    // Método privado para salvar no Firestore (só é chamado se o upload der certo)
    private void saveToFirestore(String themeId, String palavra, String silabica,
                                 String imageUrl, String audioUrl, UploadCallback callback) {

        DocumentReference themeRef = FirebaseFirestore.getInstance().collection("themes").document(themeId);

        // Cria o objeto para salvar
        Map<String, Object> newItem = new HashMap<>();
        newItem.put("palavra", palavra);
        newItem.put("silabica", silabica);
        newItem.put("imageUrl", imageUrl);
        newItem.put("audioUrl", audioUrl);

        // Adiciona ao array 'items' dentro do tema
        themeRef.update("items", FieldValue.arrayUnion(newItem))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure("Erro ao salvar dados no banco."));
    }
}

