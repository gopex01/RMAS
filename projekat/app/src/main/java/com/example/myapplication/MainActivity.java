package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextInputEditText inputEmail,inputPassword;
    Button buttonLogin,buttonSignup;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEmail=findViewById(R.id.textInputUsername);
        inputPassword=findViewById(R.id.textInputPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        buttonSignup=findViewById(R.id.buttonSignUp);
        auth=FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String catchEmail,catchPassword;
                catchPassword=inputPassword.getText().toString();
                catchEmail=inputEmail.getText().toString();
                if(!catchEmail.equals("") && !catchPassword.equals("")) {
                    Login(catchEmail, catchPassword);
                   // Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                   // startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this,"Please enter username and password",Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
    }
    public void Login(String email,String pass)
    {
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    /*Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);*/
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this,"Login is successfull",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"Login is not successfull",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}