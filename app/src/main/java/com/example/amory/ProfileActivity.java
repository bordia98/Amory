package com.example.amory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private DatabaseReference UserData;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final TextView Name_= findViewById(R.id.Name_);
        final TextView CityState_= findViewById(R.id.City_);
        final TextView Age = findViewById(R.id.Age_);
        final TextView Gender = findViewById(R.id.Gender_);
        final TextView Bio = findViewById(R.id.Bio_);
        progressBar = findViewById(R.id.pgbar);
        final CircleImageView Pic = findViewById(R.id.profile_image);

        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        UserData = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        UserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> id = dataSnapshot.getChildren();
                for(DataSnapshot d : id){
                    Name_.setText(d.child("Name").getValue().toString());
                    CityState_.setText(d.child("City").getValue().toString() + " " + d.child("State").getValue().toString());
                    Age.setText(d.child("Age").getValue().toString());
                    Gender.setText(d.child("Gender").getValue().toString());
                    Bio.setText(d.child("Description").getValue().toString());

                    Glide.with(getApplicationContext()).load(d.child("url").getValue().toString())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(Pic);

                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error Ecountered",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
