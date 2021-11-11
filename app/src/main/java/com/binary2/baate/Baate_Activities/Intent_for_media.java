package com.binary2.baate.Baate_Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.binary2.baate.Baate_Adapters.UsersAdapter;
import com.binary2.baate.Baate_Models.User;
import com.binary2.baate.R;
import com.binary2.baate.databinding.ActivityIntentForMediaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.platforminfo.UserAgentPublisher;

import java.util.ArrayList;

public class Intent_for_media extends AppCompatActivity {
FirebaseAuth auth;
ActivityIntentForMediaBinding binding;
UsersAdapter usersAdapter;
    ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntentForMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(this , users);

    }
}