package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.ViewHolder> {
    List<String> userList;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference reference;
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
                holder.textViewNaziv.setText(naziv);
                holder.textViewRadnoVreme.setText(radnoVreme);

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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNaziv=itemView.findViewById(R.id.textViewNazivTeretane);
            textViewRadnoVreme=itemView.findViewById(R.id.textViewRadnoVreme);
        }
    }



}
