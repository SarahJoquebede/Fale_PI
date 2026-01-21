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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import br.edu.ifrn.sc.info.dominio.Paciente;

// OBSERVAÇÃO: Se a classe Paciente e CadastrarPacienteActivity
// estão nesta mesma pasta (br.edu.ifrn.sc.info),
// você NÃO deve fazer o import delas. O Java já as reconhece.

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
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_paciente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (lista == null || lista.isEmpty()) return;

        Paciente paciente = lista.get(position);

        holder.tvNome.setText(paciente.getNome());
        holder.tvIdade.setText(paciente.getIdade());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CadastrarPacienteActivity.class);
            intent.putExtra("idPaciente", paciente.getId());
            context.startActivity(intent);
        });

        // --- NOVO: LÓGICA PARA O BOTÃO DE AVALIAR ---
        holder.ibAvaliar.setOnClickListener(v -> {
            // 1. Cria a Intent para a tela de áudios
            Intent intent = new Intent(context, ListAudioActivity.class);

            // 2. Passa o ID e o Nome do paciente clicado para a próxima tela
            intent.putExtra("PACIENTE_ID", paciente.getId());
            intent.putExtra("PACIENTE_NOME", paciente.getNome());

            // 3. Inicia a ActivityListAudios
            context.startActivity(intent);
        });
        holder.ibExcluir.setOnClickListener(v -> {
            // Mostra um diálogo de confirmação antes de excluir
            new AlertDialog.Builder(context)
                    .setTitle("Confirmar Exclusão")
                    .setMessage("Tem certeza que deseja excluir o paciente " + paciente.getNome() + "?")
                    .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                        // Se o usuário clicar "Sim", chama a função para deletar
                        excluirPaciente(paciente, position);
                    })
                    .setNegativeButton("Não", null) // "Não" não faz nada
                    .show();
        });

    }

    // --- NOVO: FUNÇÃO PARA EXCLUIR O PACIENTE ---
    private void excluirPaciente(Paciente paciente, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Deleta o documento do paciente da coleção "usuarios" (ou "pacientes")
        // IMPORTANTE: Ajuste "usuarios" para o nome correto da sua coleção de usuários/pacientes
        db.collection("pacientes").document(paciente.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Se a exclusão no Firestore funcionar:
                    // 1. Remove o paciente da lista local
                    lista.remove(position);
                    // 2. Notifica o RecyclerView que um item foi removido naquela posição
                    notifyItemRemoved(position);
                    // 3. (Opcional) Atualiza as posições dos itens restantes
                    notifyItemRangeChanged(position, lista.size());
                    Toast.makeText(context, "Paciente excluído com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Se a exclusão no Firestore falhar:
                    Toast.makeText(context, "Erro ao excluir o paciente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
