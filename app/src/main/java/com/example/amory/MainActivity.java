package com.example.amory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user == null){
            Intent i = new Intent(this,Login.class);
            startActivity(i);
        }
        setContentView(R.layout.activity_main);

        // For User Bio
//        Intent i  = new Intent(getApplicationContext(),UserBio.class);
//        startActivity(i);

        // For Uploading Photo
        Intent i = new Intent(getApplicationContext(),UploadPic.class);
        startActivity(i);
    }
}
