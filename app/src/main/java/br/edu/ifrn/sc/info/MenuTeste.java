package br.edu.ifrn.sc.info;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import br.edu.ifrn.sc.info.databinding.ActivityTesteMenuBinding;

public class MenuTeste extends AppCompatActivity {
    ActivityTesteMenuBinding biding;

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
}
