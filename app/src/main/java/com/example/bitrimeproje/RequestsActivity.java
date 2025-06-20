package com.example.bitrimeproje;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RequestsActivity extends AppCompatActivity {

    private EditText editTextRequestDescription;
    private Button buttonSubmitRequest;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        db = FirebaseFirestore.getInstance();
        editTextRequestDescription = findViewById(R.id.editTextRequestDescription);
        buttonSubmitRequest = findViewById(R.id.buttonSubmitRequest);

        buttonSubmitRequest.setOnClickListener(view -> addRequest());
    }

    private void addRequest() {
        String description = editTextRequestDescription.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Lütfen bir açıklama girin!", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();
        String username = "Kullanıcı Adı"; // Gerçek kullanıcı adını buraya ekleyebilirsin

        Map<String, Object> request = new HashMap<>();
        request.put("id", id);
        request.put("username", username);
        request.put("description", description);
        request.put("timestamp", System.currentTimeMillis());

        db.collection("requests").document(id)
                .set(request)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Talep başarıyla eklendi!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
