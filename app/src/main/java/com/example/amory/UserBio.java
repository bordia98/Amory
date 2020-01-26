package com.example.amory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserBio extends AppCompatActivity {

    EditText name,city,state,age,description,number;
    String gender;
    RadioButton male,female,others;
    Button next;
    FirebaseAuth mAuth;
    ProgressBar pgbar;
    private DatabaseReference UserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_bio);
        mAuth = FirebaseAuth.getInstance();
        name = findViewById(R.id.name);
        city = findViewById(R.id.city);
        state = findViewById(R.id.city);
        age = findViewById(R.id.age);
        pgbar = findViewById(R.id.pgbar);
        description = findViewById(R.id.description);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        others = findViewById(R.id.others);
        next = findViewById(R.id.Next);
        number = findViewById(R.id.number);

        if(mAuth.getCurrentUser() == null){
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
        }
        FirebaseUser user = mAuth.getCurrentUser();
        UserData = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savebio();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void savebio() {
        String tname,tcity,tstate,tgender,num;

        tname = name.getText().toString().trim();
        if(tname.length() == 0){
            name.setError("Can't Be empty");
            name.requestFocus();
            return;
        }
        tcity = city.getText().toString().trim();
        if(tcity.length() == 0){
            name.setError("Can't Be empty");
            name.requestFocus();
            return;
        }
        tstate = state.getText().toString().trim();
        if(tstate.length() == 0){
            name.setError("Can't Be empty");
            name.requestFocus();
            return;
        }
        int tage = Integer.parseInt(age.getText().toString().trim());
        if(tage <= 0){
            name.setError("Can't Be less than zero");
            name.requestFocus();
            return;
        }
        if(male.isChecked()){
            gender = "Male";
        }else if(female.isChecked()){
            gender = "female";
        }else if(others.isChecked()){
            gender = "others";
        }else{
            Toast.makeText(getApplicationContext(),"Please select gender",Toast.LENGTH_SHORT).show();
            return;
        }
        String des = description.getText().toString().trim();
        if(des.length()<10){
            description.setError("Must have atleast 10 characters");
            description.requestFocus();
            return;
        }

        num = number.getText().toString();
        if(num.length()!=10){
            number.setError("Must have 10 digit only");
            number.requestFocus();
            return;
        }


        final DatabaseReference data = UserData.push();

        final Map structure = new HashMap();
        structure.put("Name", tname);
        structure.put("City", tcity);
        structure.put("State", tstate);
        structure.put("Age",tage);
        structure.put("Gender",gender);
        structure.put("url","");
        structure.put("Description",des);
        structure.put("Number",num);

        pgbar.setVisibility(View.VISIBLE);

        Thread mainthread = new Thread(new Runnable() {
            @Override
            public void run() {
                data.setValue(structure).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pgbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Profile has been saved", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                        } else {
                            pgbar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error in saving notes", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mainthread.start();

    }
}
