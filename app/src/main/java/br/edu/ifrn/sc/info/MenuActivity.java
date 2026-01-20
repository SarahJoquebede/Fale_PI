package br.edu.ifrn.sc.info;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Encontra o componente visual do menu inferior no layout pelo seu ID correto
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Encontra o container onde as telas (fragments) serão carregadas
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_home);

        // Pega o "cérebro" da navegação de dentro do container
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Conecta o menu ao controlador para que a navegação seja automática
        // Ao clicar em um item do menu, o NavController procura um destino com o mesmo ID
        // no seu 'mobile_navigation.xml' e navega até ele.
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
