package com.example.bitrimeproje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private TextView textViewKayit;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail2);
        editTextPassword = findViewById(R.id.editTextPassword2);
        btnLogin = findViewById(R.id.btnLogin);
        textViewKayit = findViewById(R.id.textViewKayit);

        // Giriş butonu tıklanınca giriş işlemi başlat
        btnLogin.setOnClickListener(v -> loginUser());

        // Kayıt ekranına yönlendirme
        textViewKayit.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Boş alanları kontrol et
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("E-posta giriniz!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Şifre giriniz!");
            return;
        }

        // Firebase ile giriş yap
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(LoginActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Giriş başarısız! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Burada otomatik yönlendirme kaldırıldı
    }
}
