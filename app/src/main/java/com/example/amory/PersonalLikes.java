package com.example.amory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PersonalLikes extends AppCompatActivity {

    String key;
    String  otherkey;
    FirebaseAuth mAuth;
    DatabaseReference database;
    DatabaseReference database2;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_likes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        key = getIntent().getStringExtra("key");
        otherkey = getIntent().getStringExtra("otherkey");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
        }
        database = FirebaseDatabase.getInstance().getReference().child("FRIENDLIST").child(user.getUid()).child("Personal");
        final DatabaseReference data = database.push();

        final Map structure = new HashMap();
        structure.put("Key", otherkey);


        Thread mainthread = new Thread(new Runnable() {
            @Override
            public void run() {
                data.setValue(structure).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mainthread.run();
        database2 = FirebaseDatabase.getInstance().getReference().child("FRIENDLIST").child(otherkey).child("Others");
        final DatabaseReference data2 = database.push();

        final Map structure2 = new HashMap();
        structure2.put("Key", key);


        Thread mainthread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                data2.setValue(structure2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        mainthread2.run();
    }
}
