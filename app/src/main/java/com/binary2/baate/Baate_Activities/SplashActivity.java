package com.binary2.baate.Baate_Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.binary2.baate.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
SharedPreferences preferences;
SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferences = this.getSharedPreferences("Splash" , MODE_PRIVATE);
        editor  = preferences.edit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(preferences.getBoolean("isMain" , false)){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    editor.putBoolean("isMain" , true);
                    editor.apply();


                    TaskStackBuilder.create(SplashActivity.this)
                            .addNextIntentWithParentStack(new Intent(SplashActivity.this,MainActivity.class))
                            .addNextIntent(new Intent(SplashActivity.this,OnboardingActivity.class))
                            .startActivities();
                }
            }
        },2000);
    }
}