package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeretanaMapActivity extends FragmentActivity implements OnMapReadyCallback {

    //private List<String> teretane;
    private List<HashMap<String, Object>> teretane;
    private GoogleMap mMap;
    private FrameLayout map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teretana_map);
        teretane=new ArrayList<>();
        map=findViewById(R.id.map2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        DatabaseReference teretaneRef = FirebaseDatabase.getInstance().getReference("Teretane");
        teretaneRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teretane.clear();
                for (DataSnapshot teretanaSnapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> teretanaMap = (HashMap<String, Object>) teretanaSnapshot.getValue();
                    teretane.add(teretanaMap);
                }

                prikaziTeretaneNaMapi();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada greške prilikom dobijanja podataka iz baze podataka
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Podesi inicijalni prikaz mape na željenu lokaciju
        LatLng defaultLocation = new LatLng(44.7866, 20.4489);
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        prikaziTeretaneNaMapi();
    }
    private void prikaziTeretaneNaMapi() {
        if (mMap != null) {
            mMap.clear();

            for (HashMap<String, Object> teretana : teretane) {
                double latitude = 0.0;
                double longitude = 0.0;
                String naziv = "";

                Object latObject = teretana.get("latitude");
                Object lonObject = teretana.get("longitude");

                if (latObject instanceof Double) {
                    latitude = (double) latObject;
                } else if (latObject instanceof Long) {
                    latitude = ((Long) latObject).doubleValue();
                }

                if (lonObject instanceof Double) {
                    longitude = (double) lonObject;
                } else if (lonObject instanceof Long) {
                    longitude = ((Long) lonObject).doubleValue();
                }

                naziv = (String) teretana.get("naziv");

                LatLng teretanaLocation = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(teretanaLocation)
                        .title(naziv);

                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow(); // Prikazujemo info prozor odmah nakon postavljanja markera
            }
        }
    }



}
