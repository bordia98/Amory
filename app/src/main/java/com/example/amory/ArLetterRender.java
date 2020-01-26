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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArLetterRender extends AppCompatActivity {

    private static final String TAG = ArLetterRender.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    DatabaseReference database;
    DatabaseReference database2;

    private ArFragment arFragment;
    private ModelRenderable malerenderable;
    private ViewRenderable viewrenderable;
    String key;
    String otherkey;
    private DatabaseReference UserData;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView title;
    EditText description;
    CircleImageView Pic;
    boolean check=true;

    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getIntent().getStringExtra("key");
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        mAuth = FirebaseAuth.getInstance();
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

        ModelRenderable.builder()
                .setSource(this, R.raw.model)
                .build()
                .thenAccept(renderable -> malerenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        final TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
        final TransformableNode other = new TransformableNode(arFragment.getTransformationSystem());

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    count += 1;
                    if(count== 1){
                        if (viewrenderable == null) {
                            Log.d("XYZ", "Why i am here");
                            return;
                        }

                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());
                        // Create the transformable andy and add it to the anchor.
                        andy.setParent(anchorNode);

                        ((ViewRenderable) viewrenderable).setSizer(new FixedWidthViewSizer(1));
                        andy.setRenderable(viewrenderable);
                        andy.select();
                    }else if(count == 2){
                        user = mAuth.getCurrentUser();
                        andy.getParent().removeChild(andy);
                        saveData();
//                        Anchor anchor = hitResult.createAnchor();
//                        AnchorNode anchorNode = new AnchorNode(anchor);
//                        anchorNode.setParent(arFragment.getArSceneView().getScene());
//                        other.getScaleController();
//                        other.setParent(anchorNode);
//                        other.setRenderable(malerenderable);
//                        other.select();
                    }else{
                        other.getParent().removeChild(other);
                        Toast.makeText(getApplicationContext(),"You Liked the Letter",Toast.LENGTH_SHORT).show();
                        check = false;
                    }
                    Log.d("XYZ",count+"");
                });
    }

    private void saveData() {

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
        final DatabaseReference data2 = database2.push();

        final Map structure2 = new HashMap();
        structure2.put("Key", user.getUid());


        Thread mainthread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                data2.setValue(structure2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        mainthread2.run();

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
                            otherkey = let.child("Signature").getValue().toString();
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
