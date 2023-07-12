package com.example.myapplication;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.sql.StatementEvent;

public class CreateGymActivity extends AppCompatActivity {

    EditText nazivGym,radnoVreme,clanarina;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String autor="NIKO";
    int points=0;
    double userLatitude;
    double userLongitude;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currentUser;
    Button dodajTeretanu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gym);
        auth = FirebaseAuth.getInstance();
        nazivGym=findViewById(R.id.editTextNazivGym);
        radnoVreme=findViewById(R.id.editTextRadnoVreme);
        clanarina=findViewById(R.id.editTextMemebershipfee);
        dodajTeretanu=findViewById(R.id.buttonAddGym);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        currentUser = auth.getCurrentUser();
        getUserInfo();
        getUserLocation();
        Log.d("lat",String.valueOf(userLatitude));
        Log.d("lon", String.valueOf(userLongitude));
        dodajTeretanu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catchNaziv=nazivGym.getText().toString();
                String catchRadnoVreme=radnoVreme.getText().toString();
                String catchClanarina=clanarina.getText().toString();

                Log.d("Autor",autor);
                if(!catchNaziv.equals("") && !catchClanarina.equals("") && !catchRadnoVreme.equals(""))
                {
                    addTeretanu(catchNaziv,catchRadnoVreme,catchClanarina,autor,userLongitude,userLatitude);
                }
                else{
                    Toast.makeText(CreateGymActivity.this,"Neuspesno dodavanje teretane",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addTeretanu(String name,String radnoVreme,String clanarina,String autor,Double lon,Double lat) {
        DatabaseReference teretaneRef = reference.child("Teretane"); // Referenca na čvor "Teretane"

        String teretanaKey = teretaneRef.push().getKey(); // Generiranje jedinstvenog ključa za novu teretanu

        DatabaseReference novaTeretanaRef = teretaneRef.child(teretanaKey); // Referenca na novu teretanu

        novaTeretanaRef.child("autor").setValue(autor); // Postavljanje vrijednosti atributa "autor"
        novaTeretanaRef.child("naziv").setValue(name); // Postavljanje vrijednosti atributa "naziv"
        novaTeretanaRef.child("radnoVreme").setValue(radnoVreme); // Postavljanje vrijednosti atributa "radnoVreme"
        novaTeretanaRef.child("latitude").setValue(lat);
        novaTeretanaRef.child("longitude").setValue(lon);
        novaTeretanaRef.child("ocene").setValue(0);
        novaTeretanaRef.child("clanarina").setValue(clanarina) // Postavljanje vrijednosti atributa "clanarina"
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateGymActivity.this, "Teretana uspešno dodana", Toast.LENGTH_SHORT).show();
                        incrementPoints();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateGymActivity.this, "Neuspešno dodavanje teretane", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getUserInfo() {
        reference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                autor = snapshot.child("userName").getValue().toString();
                points = Integer.parseInt(snapshot.child("points").getValue().toString());

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });

    }
    public void incrementPoints() {
        DatabaseReference userRef = reference.child("Users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Dohvatiti trenutnu vrednost atributa "points"
                    int currentPoints = snapshot.child("points").getValue(Integer.class);
                        // Povećati vrednost atributa "points" za 1
                        int newPoints = currentPoints + 10;
                        userRef.child("points").setValue(newPoints)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(CreateGymActivity.this, "Points incremented successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CreateGymActivity.this, "Failed to increment points", Toast.LENGTH_SHORT).show();
                                    }
                                });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateGymActivity.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                       userLatitude= location.getLatitude();
                        userLongitude = location.getLongitude();
                    }
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



}