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

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button buttonSviKorisnici;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth= FirebaseAuth.getInstance();
        buttonSviKorisnici=findViewById(R.id.buttonVidiSveKorisnike);
        buttonSviKorisnici.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,AllUsersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.profile_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(item.getItemId()==R.id.menuItemProfile)
        {
            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
        }
        if(item.getItemId()==R.id.menuItemLogOut)
        {
                auth.signOut();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
        }
        if(item.getItemId()==R.id.menuItemSettings)
        {
            Toast.makeText(LoginActivity.this,"UUSEPH",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}