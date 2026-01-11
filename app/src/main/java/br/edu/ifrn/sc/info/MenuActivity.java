package br.edu.ifrn.sc.info;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment; // Importe este
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// A MenuActivity agora serve como um "container" para os fragments e o menu.
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. Aponta para o XML que tem o "espaço" do fragmento e o menu
        setContentView(R.layout.activity_menu);

        // 2. Encontra o NavHostFragment (o container das suas telas)
        // O ID 'nav_host_fragment' é o mesmo que definimos no FragmentContainerView do seu XML.
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        // 3. Pega o NavController, que é o "cérebro" que gerencia a troca de telas
        NavController navController = navHostFragment.getNavController();

        // 4. Encontra o BottomNavigationView pelo ID correto
        // O ID correto no seu layout é 'bottom_navigation'.
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 5. Conecta o menu ao NavController. ESSA É A LINHA PRINCIPAL!
        // Ela faz com que, ao clicar em um item do menu, o NavController
        // procure um destino com o mesmo ID no seu 'mobile_navigation.xml' e navegue sozinho.
        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
