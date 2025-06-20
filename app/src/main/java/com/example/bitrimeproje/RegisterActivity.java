package com.example.bitrimeproje;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.bitrimeproje.model.User;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword, editTextName, editTextPhone;
    private Button buttonRegister;
    private TextView textViewLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase başlatma
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // XML öğelerini bağlama
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(v -> registerUser());

        textViewLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean isEmailUsed = !task.getResult().getSignInMethods().isEmpty();
                if (isEmailUsed) {
                    Toast.makeText(RegisterActivity.this, "Bu e-posta adresi zaten kayıtlı!", Toast.LENGTH_SHORT).show();
                } else {
                    createUserWithEmail(email, password, name, phone);
                }
            } else {
                Toast.makeText(RegisterActivity.this, "E-posta kontrolü başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createUserWithEmail(String email, String password, String name, String phone) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    User newUser = new User(name, email, phone);
                    databaseReference.child(user.getUid()).setValue(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(RegisterActivity.this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(RegisterActivity.this, "Veri kaydı başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}