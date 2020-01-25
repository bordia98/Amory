package com.example.amory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Add_Letter extends AppCompatActivity  {

    EditText title, description;
    Button save;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private DatabaseReference UserData;
    double lat,lng;
    boolean check=false;
    TextView lati,longi;
    ProgressBar pgbar;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__letter);

        mAuth = FirebaseAuth.getInstance();
        description = findViewById(R.id.description);
        title = findViewById(R.id.title);
        lati = findViewById(R.id.lat);
        longi = findViewById(R.id.lng);
        pgbar = findViewById(R.id.pgbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(mAuth.getCurrentUser() == null){
            Intent i = new Intent(getApplicationContext(),Login.class);
            startActivity(i);
        }
        save = findViewById(R.id.save);
        FirebaseUser user = mAuth.getCurrentUser();
        UserData = FirebaseDatabase.getInstance().getReference().child("Letters").child(user.getUid());
        getLastLocation();
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                callsave();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void callsave() {
        String ttitle,tdescription;
        ttitle = title.getText().toString().trim();
        tdescription = description.getText().toString().trim();

        if(ttitle.length()<5){
            title.setError("Length must be greater than 5");
            title.requestFocus();
            return;
        }

        if(tdescription.length() < 12){
            description.setError("Write something which other should read");
            description.requestFocus();
            return;
        }

        if(!check){
            Toast.makeText(getApplicationContext(),"Please wait while your location get Detected",Toast.LENGTH_SHORT).show();
            return;
        }
        final Map structure = new HashMap();
        structure.put("Title", ttitle);
        structure.put("Description", tdescription);
        structure.put("ExpireTime", 100);
        structure.put("CreationTime",0);
        structure.put("Signature",mAuth.getCurrentUser().getUid());
        structure.put("Latitude",lat);
        structure.put("Longitude",lng);

        final DatabaseReference data = UserData.push();

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
                            Intent i = new Intent(getApplicationContext(),UploadPic.class);
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

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude();
            lng = mLastLocation.getLongitude();
            lati.setText(lat+"");
            longi.setText(lng+"");
            check=true;

        }
    };

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();
                                    lati.setText(lat+"");
                                    longi.setText(lng+"");
                                    check = true;
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(this,Add_Letter.class);
                startActivity(i);
            }else{
                requestPermissions();
            }
        }
    }
}
