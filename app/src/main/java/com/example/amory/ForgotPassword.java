package com.example.amory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    EditText emailfield;
    Button submit;

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        bar = (ProgressBar) findViewById(R.id.pgbar);
        bar.setVisibility(View.GONE);
        emailfield = (EditText)findViewById(R.id.emailfield);

        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendresetlink();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),Login.class);
        startActivity(i);
    }

    private void sendresetlink() {
        String email = emailfield.getText().toString().trim();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailfield.setError("Enter  a valid email id");
            emailfield.requestFocus();
            return;
        }
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        bar.setVisibility(View.VISIBLE);
        mauth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   emailfield.setText("");
                                                   emailfield.setEnabled(false);
                                                   submit.setEnabled(false);
                                                   bar.setVisibility(View.GONE);
                                                   Intent i = new Intent(getApplicationContext(),Login.class);
                                                   startActivity(i);
                                               }
                                               else{
                                                   bar.setVisibility(View.GONE);
                                                   Toast.makeText(getApplicationContext(),"Try again Lataer",Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       }
                );
        bar.setVisibility(View.GONE);
    }
}
