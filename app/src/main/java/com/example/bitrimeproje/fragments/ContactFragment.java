package com.example.bitrimeproje.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bitrimeproje.R;
import com.example.bitrimeproje.adapter.ContactAdapter;
import com.example.bitrimeproje.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class ContactFragment extends Fragment {
    private EditText editTextName, editTextSurname, editTextPhone, editTextEmailAddress;
    private Button buttonSave;
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<Contact> contactList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public ContactFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Kullanıcı oturum açmamış!", Toast.LENGTH_SHORT).show();
            return view;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("Contacts");

        editTextName = view.findViewById(R.id.editTextName);
        editTextSurname = view.findViewById(R.id.editTextSurname);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmailAddress = view.findViewById(R.id.editTextTextEmailAddress);
        buttonSave = view.findViewById(R.id.buttonSave);
        recyclerView = view.findViewById(R.id.recyclerViewContacts);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(getContext(), contactList);
        recyclerView.setAdapter(contactAdapter);

        buttonSave.setOnClickListener(v -> saveContact());
        loadContacts();

        return view;
    }

    private void saveContact() {
        String name = editTextName.getText().toString().trim();
        String surname = editTextSurname.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmailAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contactList.size() >= 3) {
            Toast.makeText(getContext(), "En fazla 3 kişi ekleyebilirsiniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = databaseReference.push().getKey();
        Contact contact = new Contact(id, name, surname, phone, email);  // Email alanı düzeltildi

        databaseReference.child(id).setValue(contact)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Kişi eklendi!", Toast.LENGTH_SHORT).show();
                    editTextName.setText("");
                    editTextSurname.setText("");
                    editTextPhone.setText("");
                    editTextEmailAddress.setText("");
                    Log.d("Firebase", "Kişi başarıyla kaydedildi: " + name + ", " + surname + ", " + phone + ", " + email);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Firebase", "Kayıt hatası: " + e.getMessage());
                });
    }

    private void loadContacts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    if (contact != null) {
                        contactList.add(contact);
                        Log.d("FirebaseTest", "Kişi yüklendi: " + contact.getName() + ", " + contact.getEmail()); // Log ekledik
                    }
                }

                contactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Veri yüklenirken hata oluştu!", Toast.LENGTH_SHORT).show();
                Log.e("FirebaseTest", "Veri yükleme hatası: " + error.getMessage());
            }
        });
    }

}
