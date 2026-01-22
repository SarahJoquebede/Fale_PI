package br.edu.ifrn.sc.info;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.edu.ifrn.sc.info.dominio.Paciente;

public class PacienteAdapter extends RecyclerView.Adapter<PacienteAdapter.ViewHolder> {

    private List<Paciente> lista;
    private Context context;

    public PacienteAdapter(List<Paciente> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_paciente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (lista == null || lista.isEmpty()) return;

        Paciente paciente = lista.get(position);

        holder.tvNome.setText(paciente.getNome());
        holder.tvIdade.setText(paciente.getIdade());



            // No PacienteAdapter.java, dentro do onBindViewHolder
            holder.itemView.setOnClickListener(v -> {
                if (paciente.getId() != null) {
                    Intent intent = new Intent(context, MenuTeste.class);
                    intent.putExtra("PACIENTE_ID", paciente.getId());
                    intent.putExtra("PACIENTE_NOME", paciente.getNome());

                    // Esta flag é essencial para que o Android não crie outra MenuTeste por cima da atual
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    context.startActivity(intent);
                }
            });

        // Clique no botão de avaliar (se mantiver a lógica antiga)
        holder.ibAvaliar.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListAudioActivity.class);
            intent.putExtra("PACIENTE_ID", paciente.getId());
            context.startActivity(intent);
        });

        // Cor zebrada
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.azul_claro));
        }

        // Clique no botão de excluir
        holder.ibExcluir.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir")
                    .setMessage("Deseja excluir " + paciente.getNome() + "?")
                    .setPositiveButton("Sim", (dialog, which) -> excluirPaciente(paciente, position))
                    .setNegativeButton("Não", null)
                    .show();
        });
    }

    private void excluirPaciente(Paciente paciente, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pacientes").document(paciente.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    lista.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, lista.size());
                    Toast.makeText(context, "Excluído!", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return (lista != null) ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvIdade;
        ImageButton ibAvaliar, ibExcluir;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomePaciente);
            tvIdade = itemView.findViewById(R.id.tvIdade2);
            ibAvaliar = itemView.findViewById(R.id.ibAvaliar);
            ibExcluir = itemView.findViewById(R.id.ibExcluir);
        }
    }
}