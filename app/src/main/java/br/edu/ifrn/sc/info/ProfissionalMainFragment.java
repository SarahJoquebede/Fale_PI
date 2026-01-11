package br.edu.ifrn.sc.info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import br.edu.ifrn.sc.info.R;

// Esta é a sua nova tela principal, agora como um Fragmento
public class ProfissionalMainFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Esta linha conecta a classe ao seu layout XML
        View view = inflater.inflate(R.layout.fragment_profissional_main, container, false);

        // Se você tinha algum código no onCreate da sua Activity, ele virá para cá.
        // Ex: Button btn = view.findViewById(R.id.meu_botao);

        return view;
    }
}
