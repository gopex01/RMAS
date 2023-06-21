package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateGymActivity extends AppCompatActivity {

    EditText nazivGym,radnoVreme,clanarina;
    FirebaseDatabase database;
    DatabaseReference reference;
    Button dodajTeretanu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gym);

        nazivGym=findViewById(R.id.editTextNazivGym);
        radnoVreme=findViewById(R.id.editTextRadnoVreme);
        clanarina=findViewById(R.id.editTextMemebershipfee);
        dodajTeretanu=findViewById(R.id.buttonAddGym);
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        dodajTeretanu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String autor=getIntent().getStringExtra("autor");
                String catchNaziv=nazivGym.getText().toString();
                String catchRadnoVreme=radnoVreme.getText().toString();
                String catchClanarina=clanarina.getText().toString();

                if(!catchNaziv.equals("") && !catchClanarina.equals("") && !catchRadnoVreme.equals(""))
                {
                    addTeretanu(autor,catchNaziv,catchRadnoVreme,catchClanarina);
                }
                else{
                    Toast.makeText(CreateGymActivity.this,"Neuspesno dodavanje teretane",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void addTeretanu(String name,String radnoVreme,String clanarina,String autor) {
        DatabaseReference teretaneRef = reference.child("Teretane"); // Referenca na čvor "Teretane"

        String teretanaKey = teretaneRef.push().getKey(); // Generiranje jedinstvenog ključa za novu teretanu

        DatabaseReference novaTeretanaRef = teretaneRef.child(teretanaKey); // Referenca na novu teretanu

        novaTeretanaRef.child("autor").setValue(autor); // Postavljanje vrijednosti atributa "autor"
        novaTeretanaRef.child("naziv").setValue(name); // Postavljanje vrijednosti atributa "naziv"
        novaTeretanaRef.child("radnoVreme").setValue(radnoVreme); // Postavljanje vrijednosti atributa "radnoVreme"
        novaTeretanaRef.child("clanarina").setValue(clanarina) // Postavljanje vrijednosti atributa "clanarina"
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(CreateGymActivity.this, "Teretana uspješno dodana", Toast.LENGTH_SHORT).show();
                        // Dodatne radnje koje želite izvršiti nakon uspješnog dodavanja teretane
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateGymActivity.this, "Neuspješno dodavanje teretane", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}