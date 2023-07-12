package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeretanaMapActivity extends FragmentActivity implements OnMapReadyCallback {

    //private List<String> teretane;
    private List<HashMap<String, Object>> teretane;
    private GoogleMap mMap;
    private EditText rad,naziv;
    private Button buttonSearch;
    private FrameLayout map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teretana_map);
        rad=findViewById(R.id.editTextRadius);
        naziv=findViewById(R.id.editTextNameSearch);
        buttonSearch=findViewById(R.id.buttonSearch);
        teretane=new ArrayList<>();
        map=findViewById(R.id.map2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
        dobijTrenutnuLokaciju();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rad.getText().toString().equals(""))
                dobijTrenutnuLokaciju();
                else{
                    pretraziTeretanu(naziv.getText().toString());
                }
            }
        });

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
    private void dobijTrenutnuLokaciju() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double trenutnaLatitude = location.getLatitude();
                double trenutnaLongitude = location.getLongitude();

                // Pozivamo metodu za prikaz teretana u radijusu
                double radijus = rad.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(rad.getText().toString());

                prikaziTeretaneURadijusu(trenutnaLatitude, trenutnaLongitude, radijus);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // Provjera dozvola za pristup lokaciji
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            // Ako nemate dozvolu, zatražite je od korisnika
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    private void prikaziTeretaneURadijusu(double centarLatitude, double centarLongitude, double radijus) {
        List<HashMap<String, Object>> teretaneURadijusu = new ArrayList<>();

        for (HashMap<String, Object> teretana : teretane) {
            double latitude = 0.0;
            double longitude = 0.0;

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

            double udaljenost = calculateDistance(centarLatitude, centarLongitude, latitude, longitude);

            if (udaljenost <= radijus) {
                teretaneURadijusu.add(teretana);
            }
        }

        // Ažurirajte prikaz teretana u radijusu na mapi
        if(mMap!=null)
        {
            mMap.clear();
        }
        for (HashMap<String, Object> teretana : teretaneURadijusu) {
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
            marker.showInfoWindow();
        }
    }


    // Ostatak koda...
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius Zemlje u kilometrima

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
    private void pretraziTeretanu(String nazivTeretane) {
        DatabaseReference teretaneRef = FirebaseDatabase.getInstance().getReference("Teretane");

        teretaneRef.orderByChild("naziv").equalTo(nazivTeretane).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Teretana je pronađena
                    for (DataSnapshot teretanaSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, Object> teretanaMap = (HashMap<String, Object>) teretanaSnapshot.getValue();
                        double latitude = Double.parseDouble(teretanaMap.get("latitude").toString());
                        double longitude = Double.parseDouble(teretanaMap.get("longitude").toString());

                        // Ažurirajte prikaz na mapi
                        mMap.clear();
                        LatLng teretanaLocation = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(teretanaLocation)
                                .title(nazivTeretane);
                        Marker marker = mMap.addMarker(markerOptions);
                        marker.showInfoWindow();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(teretanaLocation, 12));
                    }
                } else {
                    // Teretana nije pronađena
                    Toast.makeText(TeretanaMapActivity.this, "Teretana ne postoji.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Obrada greške prilikom dobijanja podataka iz baze podataka
                Toast.makeText(TeretanaMapActivity.this, "Greška prilikom pretraživanja teretane.", Toast.LENGTH_SHORT).show();
            }
        });
    }









}
