package br.edu.ifrn.sc.info;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.tvEmail.setText(paciente.getEmail());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CadastrarPacienteActivity.class);
            intent.putExtra("idPaciente", paciente.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (lista != null) ? lista.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomePaciente);
            tvEmail = itemView.findViewById(R.id.tvEmailPaciente);
        }
    }
}
