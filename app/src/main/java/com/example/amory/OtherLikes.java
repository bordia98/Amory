package com.example.amory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OtherLikes extends AppCompatActivity {
    ListView nearme;
    ArrayList<String> listItems;

    ArrayList<String> key_title;

    ArrayAdapter<String> adapter;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_likes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nearme = findViewById(R.id.likedbyothers);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        listItems = new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 ,
                listItems);

        nearme.setAdapter(adapter);

        retriveData();

    }

    private void retriveData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("FRIENDLIST").child(user.getUid()).child("Others");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dt = dataSnapshot.getChildren();
                for(DataSnapshot d : dt){
                    adapter.add(d.child("Key").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
