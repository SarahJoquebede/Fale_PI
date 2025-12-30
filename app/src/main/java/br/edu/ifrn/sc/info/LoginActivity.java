package br.edu.ifrn.sc.info;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
public class LoginActivity extends AppCompatActivity {

    //Iniciação dos componentes da UI e config da autenticação
    private EditText etEmail, etSenha;
    private Button btnLogin, btnRegistrar;
    private FirebaseAuth auth; //Variável para interagir com o Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Chama a implementação do metodo da classe pai (AppCompactActivity)

        setContentView(R.layout.activity_login);//Define qual arquivo xml vai abrir

        auth = FirebaseAuth.getInstance();//Obtem a instancia principal do Firebase

        //Associa as variaveis aos compoentes pelo ID
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        //Verifica se ja esta logado
        FirebaseUser currentUser = auth.getCurrentUser();

        //Se não for nulo, inicia o MainActivity e finaliza essa tela (finish();)
        if (currentUser != null) {
            startActivity(new Intent(this, BlocosActivity.class));
            finish();
        }

        //Quando esses botões forem clicados as respectivas funções vão ser executadas
        btnLogin.setOnClickListener(v -> login());
        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void login() {
        //Pega oq foi escrito nos campos
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        //Verifica se algum campo está vazio (Empty), se tiver vai mostrar uma mensagem curta (Toast)
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Tenta fazer o login, o listener Success excuta se deu crt e abre o mainActivity, se falhar mostra um Toast
        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(r -> {
                    startActivity(new Intent(this, BlocosActivity.class));
                    String email1 = r.getUser().getEmail();
                    Toast.makeText(this, email1, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void registrar() {
        //Pega oq foi escrito nos campos
        String email = etEmail.getText().toString();
        String senha = etSenha.getText().toString();

        //Verifica se algum campo está vazio (Empty), se tiver vai mostrar uma mensagem curta (Toast)
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(senha)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        //Criação de um usuário no Firebase enviando os dados pra la, o SuccessListener é executado se deu crt.
        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(r -> {
                    Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show());


    }
}
