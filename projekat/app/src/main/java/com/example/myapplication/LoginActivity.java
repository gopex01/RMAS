package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currentUser;
    String userName, points;
    Button buttonSviKorisnici, lokacijaMapa, dodajTeretanu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        buttonSviKorisnici = findViewById(R.id.buttonVidiSveKorisnike);
        lokacijaMapa = findViewById(R.id.buttonMapa);
        dodajTeretanu = findViewById(R.id.buttonDodajTeretanu);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        currentUser = auth.getCurrentUser();

        buttonSviKorisnici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AllUsersActivity.class);
                startActivity(intent);
            }
        });
        lokacijaMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        dodajTeretanu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CreateGymActivity.class);
                getUserInfo();
                intent.putExtra("autor", userName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuItemProfile) {
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
        }
        if (item.getItemId() == R.id.menuItemLogOut) {
            auth.signOut();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        if (item.getItemId() == R.id.menuItemSettings) {
            Toast.makeText(LoginActivity.this, "UUSEPH", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUserInfo() {
        reference.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue().toString();
                points = snapshot.child("points").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}