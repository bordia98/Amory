package com.example.amory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    ProgressBar pgbar;
    EditText email,password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pgbar=(ProgressBar)findViewById(R.id.pgbar);
        pgbar.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        TextView newuser = (TextView) findViewById(R.id.newuser);
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SignUP.class);
                startActivity(i);
            }
        });

        email=(EditText)findViewById(R.id.emailfield);
        password=(EditText)findViewById(R.id.passwordfield);

        TextView forgetpassword = (TextView)findViewById(R.id.forgetpassword);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ForgotPassword.class);
                startActivity(i);
            }
        });

        Button login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logintheuser();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }


    private void logintheuser() {

        String emailid = email.getText().toString().trim();
        String passwordid = password.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(emailid).matches()) {
            email.setError("Enter  a valid email id");
            email.requestFocus();
            return;
        }
        pgbar.setVisibility(View.VISIBLE);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            mAuth.signInWithEmailAndPassword(emailid, passwordid)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                pgbar.setVisibility(View.GONE);
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            } else {
                                pgbar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Please check your credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        } else {
            pgbar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"NO Internet Access",Toast.LENGTH_SHORT).show();
        }

    }
}
