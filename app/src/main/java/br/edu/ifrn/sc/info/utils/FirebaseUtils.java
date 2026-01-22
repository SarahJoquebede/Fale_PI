package br.edu.ifrn.sc.info.utils; // Verifique se o package está correto

import android.net.Uri;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtils {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface UploadCallback {
        void onSuccess(String imageUrl, String audioUrl);

        void onFailure(String errorMessage);
    }

    public void uploadActivityItem(String themeId, String palavra, String silabica, Uri imageUri, Uri audioUri, UploadCallback callback) {
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("images/" + themeId + "/" + palavra + ".jpg");
        StorageReference audioRef = storageRef.child("audios/" + themeId + "/" + palavra + ".mp3");

        // Upload da Imagem
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> {

                // Upload do Áudio
                audioRef.putFile(audioUri).addOnSuccessListener(audioSnapshot -> {
                    audioRef.getDownloadUrl().addOnSuccessListener(audioUrl -> {

                        // Salvando no Firestore (Coleção Geral Themes)
                        Map<String, Object> atividade = new HashMap<>();
                        atividade.put("palavra", palavra);
                        atividade.put("silabica", silabica);
                        atividade.put("imagemUrl", imageUrl.toString());
                        atividade.put("audioUrl", audioUrl.toString());

                        // Usamos .set com merge para CRIAR o documento se ele não existir
                        db.collection("themes")
                                .document(themeId)
                                .collection("atividades")
                                .document(palavra)
                                .set(atividade, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> callback.onSuccess(imageUrl.toString(), audioUrl.toString()))
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    });
                }).addOnFailureListener(e -> callback.onFailure("Erro no áudio: " + e.getMessage()));
            });
        }).addOnFailureListener(e -> callback.onFailure("Erro na imagem: " + e.getMessage()));
    }
}