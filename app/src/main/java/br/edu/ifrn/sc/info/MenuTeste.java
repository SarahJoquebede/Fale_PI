package br.edu.ifrn.sc.info;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import br.edu.ifrn.sc.info.databinding.ActivityTesteMenuBinding;

public class MenuTeste extends AppCompatActivity {
    ActivityTesteMenuBinding biding;

    // Variável que guarda o paciente selecionado durante a sessão
    private String pacienteSelecionadoId;

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        biding = ActivityTesteMenuBinding.inflate(getLayoutInflater());
        setContentView(biding.getRoot());

        // VERIFICAÇÃO: Se a Activity foi aberta por um clique na lista de pacientes
        if (getIntent().hasExtra("PACIENTE_ID")) {
            pacienteSelecionadoId = getIntent().getStringExtra("PACIENTE_ID");

            // 1. Abre o DashboardTeste diretamente
            replaceFragment(new DashboardTeste());
            // 2. Marca o ícone "Atividade" no menu de baixo
            biding.bottomNavigationView2.setSelectedItemId(R.id.nav_atividade);
        } else {
            // Início padrão se abrir o app normalmente
            replaceFragment(new HomeTeste());
        }

        biding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inicio) {
                replaceFragment(new HomeTeste());
            } else if (itemId == R.id.nav_atividade) {
                replaceFragment(new DashboardTeste());
            } else if (itemId == R.id.nav_meus_pacientes) {
                replaceFragment(new ListaTeste());
            }
            return true;
        });
    }


    public String getPacienteSelecionadoId() {
        return pacienteSelecionadoId;
    }
}