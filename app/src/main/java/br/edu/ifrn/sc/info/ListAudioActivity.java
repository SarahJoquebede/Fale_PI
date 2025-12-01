package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
public class ListAudioActivity extends AppCompatActivity{
    private RecyclerView rvAudios;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private List<AudioItem> audioList = new ArrayList<>();
    private AudioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_audios);

        rvAudios = findViewById(R.id.rvAudios);
        rvAudios.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

     //   loadAudios();

        adapter = new AudioAdapter(audioList, item -> {
            Intent i = new Intent(this, ReviewActivity.class);
            i.putExtra("audioUrl", item.getArquivoUrl());
            i.putExtra("audioId", item.getId());
            startActivity(i);
        });
        rvAudios.setAdapter(adapter);

        loadAudios();
    }

    private void loadAudios() {
        db.collection("audios")
                .whereNotEqualTo("autorId", user.getUid())
                .get()
                .addOnSuccessListener(q -> {
                    audioList.clear();
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        AudioItem a = new AudioItem();
                        a.setId(doc.getId());
                        a.setArquivoUrl(doc.getString("arquivoUrl"));
                        a.setAutorEmail(doc.getString("autorEmail"));
                        audioList.add(a);
                    }
                   adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private static class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {
        interface OnClick {
            void onClick(AudioItem item);
        }

        private final List<AudioItem> list;
        private final OnClick click;

        AudioAdapter(List<AudioItem> list, OnClick click) {
            this.list = list;
            this.click = click;
        }

        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = View.inflate(parent.getContext(), R.layout.audio_item, null);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            AudioItem a = list.get(pos);
            h.tvTitle.setText(a.getAutorEmail());
            h.tvSubtitle.setText(a.getArquivoUrl());
            h.btnOpen.setOnClickListener(v -> click.onClick(a));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvSubtitle;
            Button btnOpen;
            ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                tvSubtitle = v.findViewById(R.id.tvSubtitle);
                btnOpen = v.findViewById(R.id.btnOpen);
            }
        }
    }

}
