package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ForgetpasswordActivity extends AppCompatActivity {

    Button buttonResetpass;
    TextInputEditText inputEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        buttonResetpass=findViewById(R.id.buttonResetPassword);
        inputEmail=findViewById(R.id.textinputEmail);
    }
}