package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView username,points,name;
    Button buttonUpdate;
    CircleImageView profilePhoto;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username=findViewById(R.id.textViewUsername);
        name=findViewById(R.id.textViewName);
        points=findViewById(R.id.textViewPoints);
        buttonUpdate=findViewById(R.id.buttonUpdateProfile);
        profilePhoto=findViewById(R.id.circleImageViewProfile);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        user=auth.getCurrentUser();
        getUserInfo();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserInfo();
            }
        });
    }
    public void getUserInfo()
    {
        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String usname=snapshot.child("userName").getValue().toString();
                String photo=snapshot.child("image").getValue().toString();
                String namec=snapshot.child("name").getValue().toString();
                String pointsc=snapshot.child("points").getValue().toString();
                username.setText("Username: "+usname);
                name.setText("Name & surname: "+namec);
                points.setText("Points: "+pointsc);
                if(photo.equals("null")){
                    profilePhoto.setImageResource(R.drawable.login);
                }
                else{
                    Picasso.get().load(photo).into(profilePhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}