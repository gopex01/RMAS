package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> {
    List<String> userList;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    public GymAdapter(List<String>uL,Context mC)
    {
        this.userList=uL;
        this.mContext=mC;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.gym_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        reference.child("Teretane").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String naziv=snapshot.child("naziv").getValue().toString();
                String radnoVreme=snapshot.child("radnoVreme").getValue().toString();
                String prosecnaOcena=snapshot.child("ocene").getValue().toString();
                holder.textViewNaziv.setText("Naziv:"+naziv);
                holder.textViewRadnoVreme.setText("Radno vreme:"+radnoVreme);
                holder.textViewProsecnaOcena.setText("Prosecna ocena:"+prosecnaOcena);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder
    {
        private TextView textViewNaziv;
        private TextView textViewRadnoVreme;
        private TextView textViewProsecnaOcena;
        private Button dodajOcenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNaziv=itemView.findViewById(R.id.textViewNazivTeretane);
            textViewRadnoVreme=itemView.findViewById(R.id.textViewRadnoVreme);
            textViewProsecnaOcena=itemView.findViewById(R.id.prosecnaOcena);
            dodajOcenu=itemView.findViewById(R.id.buttonDodajOcenuTeretani);
            EditText ocena=itemView.findViewById(R.id.ocena);
            dodajOcenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("naziv:",textViewNaziv.getText().toString());
                    OceniTeretanu(textViewNaziv.getText().toString(),ocena.getText().toString());
                    incrementPoints();
                    Toast.makeText(mContext.getApplicationContext(), "Uspesno ste dali ocenu!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void OceniTeretanu(String name, String novaOcena) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Teretane");
        Query query = usersRef.orderByChild("naziv").equalTo(name);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String gymId = childSnapshot.getKey();
                    DatabaseReference gymRef = usersRef.child(gymId);

                    gymRef.child("ocene").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                int staraOcena = dataSnapshot.getValue(Integer.class);
                                int novaOcenaInt = Integer.parseInt(novaOcena);

                                // Izračun nove ocjene
                                int novaVrednostOcene;
                                if(staraOcena!=0) {
                                     novaVrednostOcene = (staraOcena + novaOcenaInt) / 2;
                                }
                                else{
                                     novaVrednostOcene=novaOcenaInt;
                                }

                                // Postavljanje nove vrijednosti ocene
                                gymRef.child("ocene").setValue(novaVrednostOcene)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    // Uspješno promijenjena vrijednost
                                                    Log.e("tag", "Uspješno promijenjena vrijednost ocene");
                                                } else {
                                                    // Greška pri promjeni vrijednosti ocene
                                                    Log.e("tag2", "Neuspješno promijenjena vrijednost ocene");
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // U slučaju pogreške pri dohvatu podataka iz baze
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void incrementPoints() {
        DatabaseReference userRef = reference.child("Users").child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Dohvatiti trenutnu vrednost atributa "points"
                    int currentPoints = snapshot.child("points").getValue(Integer.class);
                    // Povećati vrednost atributa "points" za 1
                    int newPoints = currentPoints + 5;
                    userRef.child("points").setValue(newPoints)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                   Log.e("usp","Uspesno povecani bodovi");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@android.support.annotation.NonNull Exception e) {
                                     Log.e("neusp","Neuspesno povecani bodovi");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
               Log.e("tr","error");
            }
        });
    }




}
