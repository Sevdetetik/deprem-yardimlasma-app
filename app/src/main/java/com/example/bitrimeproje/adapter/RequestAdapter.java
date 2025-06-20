package com.example.bitrimeproje.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitrimeproje.R;
import com.example.bitrimeproje.model.Request;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private List<Request> requestList;
    private String currentUserId;
    private Context context;

    public RequestAdapter(List<Request> requestList, String currentUserId, Context context) {
        this.requestList = requestList;
        this.currentUserId = currentUserId;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestList.get(position);
        holder.tvFullName.setText(request.getUsername());
        holder.tvPhone.setText(request.getPhoneNumber());
        holder.tvDescription.setText(request.getDescription());

        // Eğer bu isteği görüntüleyen kullanıcı kendi isteğini görüyorsa silme butonunu göster
        if (request.getUserId().equals(currentUserId)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                FirebaseFirestore.getInstance().collection("Requests")
                        .document(request.getRequestId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(context, "İstek silindi", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Silme işlemi başarısız!", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public void updateList(List<Request> newList) {
        requestList = newList;
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvPhone, tvDescription;
        ImageButton btnDelete;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.textViewUsername);
            tvPhone = itemView.findViewById(R.id.textViewPhone);
            tvDescription = itemView.findViewById(R.id.textViewDescription);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

    }
}
