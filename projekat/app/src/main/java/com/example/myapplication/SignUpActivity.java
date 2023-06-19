package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import javax.sql.RowSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSignUp;
    TextInputEditText newEmail,newPassword,newUsername;
    CircleImageView regPhoto;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Uri imageUri;
    boolean imageControl=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        buttonSignUp=findViewById(R.id.buttonSignUpR);
        newEmail=findViewById(R.id.EmailSignUp);
        newPassword=findViewById(R.id.PasswordSignUp);
        newUsername=findViewById(R.id.UsernameSignUp);
        regPhoto=findViewById(R.id.circleImageViewSignUp);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        regPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String catchPass,catchUser,catchEmail;
                catchEmail=newEmail.getText().toString();
                catchUser=newUsername.getText().toString();
                catchPass=newPassword.getText().toString();
                if(!catchEmail.equals("") && !catchUser.equals("") && !catchPass.equals(""))
                {
                        signUp(catchEmail,catchPass,catchUser);
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Please enter Email,Username and Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void signUp(String email,String pass,String user)
    {
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(user);
                    reference.child("Users").child(auth.getUid()).child("points").setValue(0);
                    if(imageControl)
                    {
                        UUID randomId= UUID.randomUUID();
                        String imageName="images/"+randomId+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                StorageReference myStorageReference=firebaseStorage.getReference(imageName);
                                myStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath=uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is successfull",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this,"Write to database is not successfull",Toast.LENGTH_SHORT).show();
                                                Log.e("tag","Nespesno upisano u bazu");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else{
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                    intent.putExtra("userName",user);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SignUpActivity.this,task.getResult().describeContents(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void imageChooser()
    {
        Intent intentImage=new Intent();
        intentImage.setType("image/*");
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentImage,1);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            Picasso.get().load(imageUri).into(regPhoto);
            imageControl=true;
        }
        else{
            imageControl=false;
        }
    }
}