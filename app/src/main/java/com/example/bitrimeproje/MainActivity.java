package com.example.bitrimeproje;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bitrimeproje.fragments.ContactFragment;
import com.example.bitrimeproje.fragments.HomeFragment;
import com.example.bitrimeproje.fragments.RequestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase bildirim konusu aboneliği
        FirebaseMessaging.getInstance().subscribeToTopic("earthquake")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful()
                            ? "Deprem konusuna abone olundu."
                            : "Abonelik başarısız!";
                    Log.d(TAG, msg);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                });

        // Firebase arka plan servisini başlat
        startFirebaseService();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_contacts) {
                selectedFragment = new ContactFragment();
            } else if (item.getItemId() == R.id.nav_requests) {
                selectedFragment = new RequestsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    private void startFirebaseService() {
        Intent serviceIntent = new Intent(this, FirebaseService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}
