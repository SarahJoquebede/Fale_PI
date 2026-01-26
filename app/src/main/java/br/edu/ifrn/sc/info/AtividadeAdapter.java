package br.edu.ifrn.sc.info;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.edu.ifrn.sc.info.Atividade;

// Importe sua classe de modelo aqui (ajuste o pacote se necessário)


public class AtividadeAdapter extends RecyclerView.Adapter<AtividadeAdapter.ViewHolder> {
    private List<Atividade> lista;
    private Context context;

    public AtividadeAdapter(List<Atividade> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Agora o LayoutInflater será reconhecido
        View view = LayoutInflater.from(context).inflate(R.layout.item_atividade, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Atividade atividade = lista.get(position);
        holder.txtPalavra.setText(atividade.getPalavra());
        holder.txtSilabica.setText(atividade.getSilabica());

        // Glide precisa do import acima para funcionar
        Glide.with(context).load(atividade.getImagemUrl()).into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            // Intent e MainActivity precisam estar acessíveis
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("atividade", atividade);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPalavra, txtSilabica;
        ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            txtPalavra = itemView.findViewById(R.id.txtPalavraItem);
            txtSilabica = itemView.findViewById(R.id.txtSilabicaItem);
            img = itemView.findViewById(R.id.imgItem);
        }
    }
}