package com.example.amory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class PersonalLikes extends AppCompatActivity {

    String key;
    String  otherkey;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_likes);
        key = getIntent().getStringExtra("key");
        otherkey = getIntent().getStringExtra("otherkey");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
        }
//        database = FirebaseDatabase.getInstance().getReference().child("Friend")
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
