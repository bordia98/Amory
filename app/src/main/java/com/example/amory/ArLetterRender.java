package com.example.amory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.FixedWidthViewSizer;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArLetterRender extends AppCompatActivity {

    private static final String TAG = ArLetterRender.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;
    private ViewRenderable viewrenderable;
    String key;
    private DatabaseReference UserData;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView title;
    EditText description;
    CircleImageView Pic;
    boolean check=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getStringExtra("key");
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        Log.d("XYZ",key);
        View lay = getLayoutInflater().inflate(R.layout.letterlayout,null);
        title = lay.findViewById(R.id.title);
        description = lay.findViewById(R.id.description);
        Pic = lay.findViewById(R.id.profile_image);
        UserData = FirebaseDatabase.getInstance().getReference().child("Letters");

        accessthedata();

        setContentView(R.layout.activity_ar_letter_render);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        // ViewRenderable.builder().setView(getApplicationContext(), R.raw.profile).build();

        Log.d("XYZ","Second");
        ViewRenderable.builder()
                .setView(this, lay  )
                .build()
                .thenAccept(renderable -> viewrenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });


        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if(check){
                        if (viewrenderable == null) {
                            Log.d("XYZ", "Why i am here");
                            return;
                        }

                        // Create the Anchor.
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        // Create the transformable andy and add it to the anchor.
                        TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                        andy.setParent(anchorNode);
                        ((ViewRenderable) viewrenderable).setSizer(new FixedWidthViewSizer(1));
                        andy.setRenderable(viewrenderable);
                        andy.select();
                        check = false;
                    }else{
                        Toast.makeText(getApplicationContext(),"You Liked the Letter",Toast.LENGTH_SHORT).show();

                        // Code for Matching will come here
                    }
                });
    }

    private void accessthedata() {
        UserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> mailers = dataSnapshot.getChildren();
                for(DataSnapshot d: mailers){
                    int flag = 0;
                    Iterable<DataSnapshot> letters = d.getChildren();
                    for(DataSnapshot let : letters){
                        String keyi = let.getKey().toString();
                        if(keyi.equals(key)){
                            Log.d("XYZ","First");
                            title.setText(let.child("Title").getValue().toString());
                            description.setText(let.child("Description").getValue().toString());
                            Glide.with(getApplicationContext()).load(let.child("Url").getValue().toString())
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(Pic);
                            flag = 1;
                            break;
                        }
                    }
                    if(flag == 1){
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}
