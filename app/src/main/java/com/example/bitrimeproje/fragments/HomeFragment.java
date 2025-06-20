package com.example.bitrimeproje.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.bitrimeproje.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CALL_PERMISSION = 2;

    private Button btnSendLocation, btnCall112, btnCall155;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnSendLocation = view.findViewById(R.id.btnSendLocation);
        btnCall112 = view.findViewById(R.id.btnCall112);
        btnCall155 = view.findViewById(R.id.btnCall155);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        btnSendLocation.setOnClickListener(v -> getContactsAndSendEmail());

        btnCall112.setOnClickListener(v -> makePhoneCall("112"));
        btnCall155.setOnClickListener(v -> makePhoneCall("155"));

        return view;
    }

    private void makePhoneCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void getContactsAndSendEmail() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Oturum açmış bir kullanıcı bulunamadı!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.getUid())
                .child("Contacts");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> emailList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String email = data.child("email").getValue(String.class);
                    if (email != null && !email.isEmpty()) {
                        emailList.add(email);
                    }
                }

                if (!emailList.isEmpty()) {
                    getLocationAndSendEmail(emailList);
                } else {
                    Toast.makeText(getContext(), "Kayıtlı e-posta adresi bulunamadı!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Veri okunurken hata oluştu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocationAndSendEmail(List<String> emailList) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        // GPS açık mı kontrol et
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(), "Lütfen GPS'i açın!", Toast.LENGTH_LONG).show();
            return;
        }

        // Önce son bilinen konumu gönder (hızlı tepki)
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();
            String message = "Acil Durum! Konumum (önceki): https://maps.google.com/?q=" + latitude + "," + longitude;
            sendEmail(emailList, "Acil Konum Bilgisi", message);
        }

        // Ardından güncel konum için dinleyici başlat
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "Acil Durum! Konumum (güncel): https://maps.google.com/?q=" + latitude + "," + longitude;

                sendEmail(emailList, "Acil Konum Bilgisi", message);
                locationManager.removeUpdates(this); // Güncellenince durdur
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(@NonNull String provider) {}
            @Override public void onProviderDisabled(@NonNull String provider) {}
        });
    }

    private void sendEmail(List<String> recipients, String subject, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients.toArray(new String[0]));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "E-posta Gönder"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "E-posta göndermek için bir uygulama bulunamadı!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContactsAndSendEmail();
            } else {
                Toast.makeText(requireContext(), "Konum izni reddedildi!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Arama izni verildi, tekrar deneyin.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Arama izni reddedildi!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
