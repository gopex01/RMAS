package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    EditText newUsername,newPhone,newName;
    Button saveChanges;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        newUsername=findViewById(R.id.editTextnewUserName);
        newPhone=findViewById(R.id.editTextnewPhone);
        newName=findViewById(R.id.editTextnewName);
        saveChanges=findViewById(R.id.buttonSacuvajIzmene);
        auth = FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        currentUser = auth.getCurrentUser();

        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catchUser=newUsername.getText().toString();
                String catchPhone=newPhone.getText().toString();
                String catchName=newName.getText().toString();
                saveChanges(catchUser,catchName,catchPhone);
            }
        });
    }
    public void saveChanges(String nUser,String nName,String nPhone)
    {
        DatabaseReference userRef = reference.child("Users").child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    if(!nUser.equals(""))
                    {
                        userRef.child("userName").setValue(nUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SettingsActivity.this,"Succesfull change ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    if(!nName.equals(""))
                    {
                        userRef.child("name").setValue(nName)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SettingsActivity.this,"Succesfull change ",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    if(!nPhone.equals(""))
                    {
                        userRef.child("phone").setValue(nPhone)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SettingsActivity.this,"Succesfull change",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
            }

        });


    }

}