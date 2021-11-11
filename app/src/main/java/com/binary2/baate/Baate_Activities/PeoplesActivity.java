package com.binary2.baate.Baate_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.binary2.baate.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PeoplesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peoples);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.peoplw);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.chats_baate:
                        startActivity(new Intent(getApplicationContext() , MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.calls:
                        startActivity(new Intent(getApplicationContext() , CallsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.peoplw:
                        return true;
                    case R.id.moments:
                        startActivity(new Intent(getApplicationContext() , MomentsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }





                return false;
            }
        });
    }
}