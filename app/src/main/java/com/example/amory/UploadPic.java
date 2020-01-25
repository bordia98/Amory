package com.example.amory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPic extends AppCompatActivity {

    ImageView img;
    private static final int CAMERA_REQUEST = 1888;
    Button save;
    Uri targetUri;
    private StorageReference mStorageRef;
    private  boolean im = false;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressBar pgbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pic);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        img = findViewById(R.id.image);
        img.setImageDrawable(getResources().getDrawable(R.drawable.profile));

        pgbar = findViewById(R.id.pgbar);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage();
                Log.d("XYZ", "clicked");
            }
        });

    }

    private void uploadimage() {
        if(!im){
            Toast.makeText(getApplicationContext(),"Please select image", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if(user == null){
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }

        pgbar.setVisibility(View.VISIBLE);
        final StorageReference upload = mStorageRef.child("Users").child(user.getUid()).child("profile_pic.jpg");

        UploadTask uploadTask = upload.putFile(targetUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    pgbar.setVisibility(View.GONE);
                    throw task.getException();
                }
                return upload.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    Uri taskResult = task.getResult();
                    saveinuser(taskResult);
                }
            }
        });
    }

    private void saveinuser(final Uri taskResult) {
        final DatabaseReference UserData = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        UserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> id = dataSnapshot.getChildren();
                for(DataSnapshot d : id){
                    Map updateMap = new HashMap();
                    updateMap.put("url",taskResult.toString());
                    UserData.child(d.getKey()).updateChildren(updateMap);
                }
                pgbar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"Error Ecountered",Toast.LENGTH_SHORT).show();
                pgbar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(photo);
        }

        if (resultCode == RESULT_OK){
            targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                img.setImageBitmap(bitmap);
                im = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
