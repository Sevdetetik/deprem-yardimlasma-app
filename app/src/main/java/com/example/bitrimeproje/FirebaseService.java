package com.example.bitrimeproje;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseService extends Service {

    DatabaseReference ref;

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        ref = FirebaseDatabase.getInstance().getReference("earthquake_detected");
        startForeground(1, getNotification("Deprem Takibi Aktif"));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean detected = snapshot.getValue(Boolean.class);
                if (Boolean.TRUE.equals(detected)) {
                    showNotification("Deprem Algılandı", "Sarsıntı tespit edildi!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseService", "Veri okunamadı: " + error.getMessage());
            }
        });
    }

    private Notification getNotification(String message) {
        String channelId = "earthquake_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Deprem Kanalı", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Deprem Uyarı Sistemi")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_warning)
                .build();
    }

    private void showNotification(String title, String message) {
        Object text;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "earthquake_channel")
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1002, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
