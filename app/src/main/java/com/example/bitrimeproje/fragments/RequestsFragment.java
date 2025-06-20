package com.example.bitrimeproje.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitrimeproje.R;
import com.example.bitrimeproje.adapter.RequestAdapter;
import com.example.bitrimeproje.model.Request;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private EditText editTextDescription;
    private Button btnSubmit;
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<Request> requestList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false); // LAYOUT ADI fragment_request.xml OLMALI

        editTextDescription = view.findViewById(R.id.editTextDescription);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        recyclerView = view.findViewById(R.id.recyclerViewRequests);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new RequestAdapter(requestList, auth.getCurrentUser().getUid(), getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> shareRequest());

        loadRequests();

        return view;
    }

    private void shareRequest() {
        String desc = editTextDescription.getText().toString().trim();
        FirebaseUser user = auth.getCurrentUser();

        if (!desc.isEmpty() && user != null) {
            String userId = user.getUid();
            String requestId = db.collection("Requests").document().getId();

            // Realtime DB'den kullanıcı bilgilerini çek
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String isim = snapshot.child("isim").getValue(String.class);
                    String telefon = snapshot.child("telefon").getValue(String.class);
                    String email = user.getEmail();

                    Request request = new Request(
                            requestId,
                            userId,
                            isim != null ? isim : "",
                            email != null ? email : "",
                            telefon != null ? telefon : "",
                            desc,
                            System.currentTimeMillis()
                    );

                    db.collection("Requests").document(requestId).set(request)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "İstek paylaşıldı", Toast.LENGTH_SHORT).show();
                                editTextDescription.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "İstek kaydedilemedi", Toast.LENGTH_SHORT).show();
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Kullanıcı verisi alınamadı!", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), "Açıklama boş olamaz", Toast.LENGTH_SHORT).show();
        }
    }



    private void loadRequests() {
        db.collection("Requests")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (snapshots != null && error == null) {
                        List<Request> tempList = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots) {
                            Request request = doc.toObject(Request.class);
                            tempList.add(request);
                        }
                        adapter.updateList(tempList);
                    } else {
                        Log.e("RequestDebug", "Veriler yüklenemedi: " + (error != null ? error.getMessage() : "Bilinmeyen hata"));
                    }
                });
    }
}
