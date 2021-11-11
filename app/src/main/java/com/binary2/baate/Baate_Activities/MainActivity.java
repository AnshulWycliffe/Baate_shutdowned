package com.binary2.baate.Baate_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.MenuItem;
import android.widget.Toast;

import com.binary2.baate.Baate_Adapters.UsersAdapter;
import com.binary2.baate.Baate_Models.User;
import com.binary2.baate.R;
import com.binary2.baate.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    ProgressDialog dialog;
    User user;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {


                String toolbarColor = mFirebaseRemoteConfig.getString("toolbarColor");
                String toolbarImage = mFirebaseRemoteConfig.getString("toolbarImage");
                boolean istoolBarEnabled = mFirebaseRemoteConfig.getBoolean("toolBarEnabled");
                //Toast.makeText(MainActivity.this, toolbarColor,Toast.LENGTH_LONG).show();
                //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColor)));
               if(istoolBarEnabled) {
                   Glide.with(MainActivity.this)
                           .load(toolbarImage)
                           .into(new CustomTarget<Drawable>() {
                               @Override
                               public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                   getSupportActionBar().setBackgroundDrawable(resource);
                               }

                               @Override
                               public void onLoadCleared(@Nullable Drawable placeholder) {

                               }
                           });
               }else{
                   getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(toolbarColor)));

               }

            }
        });
        database = FirebaseDatabase.getInstance();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                HashMap<String  ,  Object> map = new HashMap<>();
                map.put("token" , token);
               database.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);
           //     Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
            }
        });
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Moments.....");
        dialog.setCancelable(false);
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();

        usersAdapter = new UsersAdapter(this, users);

        binding.recyclervieww.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclervieww.setAdapter(usersAdapter);



        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User user = snapshot1.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                
            }
        });
    binding.bottomNavigationView.setSelectedItemId(R.id.chats_baate);
    binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.moments:
                    startActivity(new Intent(getApplicationContext() , MomentsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.chats_baate:
                    return true;
                case R.id.peoplw:
                    startActivity(new Intent(getApplicationContext() , PeoplesActivity.class));
                    overridePendingTransition(0,0);
                    return true;case R.id.calls:
                    startActivity(new Intent(getApplicationContext() , CallsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }




            return false;
        }


    });


    }






    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String CurrentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(CurrentId).setValue("Last Seen On " );
    }

    @Override
    protected void onStop() {

        super.onStop();
        String currentId = FirebaseAuth.getInstance().getUid();
                database.getReference().child("presence").child(currentId).setValue("Offline");
    }
}
