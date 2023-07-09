package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllGymActivity extends AppCompatActivity {

    RecyclerView rv;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseDatabase database;
    Button dodajOcenu;
    EditText ocena;
    FirebaseUser user;
    List<String> list;
    GymAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_gym);
        rv=findViewById(R.id.rvGyms);
        rv.setLayoutManager(new LinearLayoutManager(this));
        list=new ArrayList<>();
        dodajOcenu=findViewById(R.id.buttonDodajOcenuTeretani);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        user=auth.getCurrentUser();
        reference=database.getReference();
        reference.child("Users").child(user.getUid()).child("userName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getGyms();
                adapter=new GymAdapter(list,AllGymActivity.this);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void getGyms()
    {
        reference.child("Teretane").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                if (!key.equals(user.getUid())) {
                    list.add(key);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class ViewHolder extends  RecyclerView.ViewHolder
    {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dodajOcenu=itemView.findViewById(R.id.buttonDodajOcenuTeretani);
            ocena=itemView.findViewById(R.id.ocena);
            dodajOcenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(AllGymActivity.this, "Uhvaceno", Toast.LENGTH_SHORT).show();
                    Log.d("najjace","najjace123");
                }
            });
        }
    }

}