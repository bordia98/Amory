package com.example.amory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PerfectMatch extends AppCompatActivity {
    ListView nearme;
    ArrayList<String> listItems;
    ArrayList<String> key_title;
    ArrayList<String> first;
    ArrayList<String> second;
    ArrayAdapter<String> adapter;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_match);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nearme = findViewById(R.id.match);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        first = new ArrayList<String>();
        key_title = new ArrayList<String>();
        second = new ArrayList<String>();
        listItems = new ArrayList<String>();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1 ,
                listItems);

        nearme.setAdapter(adapter);

        nearme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(),ViewOtherProfile.class);
                i.putExtra("key",key_title.get(position));
                startActivity(i);
            }
        });

        retriveData();

    }

    private void retriveData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("FRIENDLIST").child(user.getUid()).child("Personal");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dt = dataSnapshot.getChildren();
                for(DataSnapshot d : dt){
                    String val =d.child("Key").getValue().toString();
                    if(val!=null)
                        first.add(val);
                }
                retriveSecondData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void retriveSecondData() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("FRIENDLIST").child(user.getUid()).child("Others");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> dt = dataSnapshot.getChildren();
                for(DataSnapshot d : dt){
                    String val =d.child("Key").getValue().toString();
                    if(val != null)
                        second.add(val);                }
                domatching();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void domatching() {
        for(String s1: first){
            for(String s2: second){
                if(s1.equals(s2)){
                    key_title.add(s1);
                }
            }
        }
        getname();
    }

    private void getname() {

        for(String key: key_title){

            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> snapshot = dataSnapshot.getChildren();
                    for (DataSnapshot r : snapshot) {
                        String val = r.child("Name").getValue().toString();
                        adapter.add(val);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
}
