package com.example.bitrimeproje.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitrimeproje.R;
import com.example.bitrimeproje.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private Context context;
    private List<Contact> contactList;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid()).child("Contacts");
        }
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.textViewName.setText(contact.getName() + " " + contact.getSurname());
        holder.textViewPhone.setText(contact.getPhone());
        holder.textViewEmail.setText(contact.getEmail()); // ✅ Email bilgisini gösteriyoruz

        // ❌ X İkonuna Basılınca Silme İşlemi
        holder.buttonDelete.setOnClickListener(v -> {
            if (databaseReference != null) {
                databaseReference.child(contact.getId()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Kişi silindi!", Toast.LENGTH_SHORT).show();
                            contactList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, contactList.size());
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Silme başarısız: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewPhone, textViewEmail; // ✅ textViewEmail eklendi
        ImageView buttonDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewEmail = itemView.findViewById(R.id.textViewEmail); // ✅ textViewEmail bağlandı
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
