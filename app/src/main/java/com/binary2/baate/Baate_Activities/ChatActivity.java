package com.binary2.baate.Baate_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.binary2.baate.Baate_Adapters.MessagesAdapter;
import com.binary2.baate.Baate_Models.Message;
import com.binary2.baate.R;
import com.binary2.baate.databinding.ActivityChatBinding;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    ImageView imageView;

    MessagesAdapter adapter;
    ArrayList<Message> messages;

    FirebaseStorage storage;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    ProgressDialog dialog;
    String senderUid;
    String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending.....");
        dialog.setCancelable(false);



        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages, senderRoom, receiverRoom);
        binding.recyclerVieww.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerVieww.setAdapter(adapter);


        String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");
        String token  = getIntent().getStringExtra("token");

        binding.name.setText(name);
        Glide.with(ChatActivity.this).load(profile)
                .placeholder(R.drawable.avatarforprofile)
                .into(binding.profile);

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


         receiverUid = getIntent().getStringExtra("uid");
         senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (!status.isEmpty()) {
                        if (status.equals("Offline")) {
                            binding.status.setText(status);
                        } else {
                            binding.status.setText(status);
                            binding.status.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        database.getReference().child("Chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.messageBox.getText())) {
                    Toast.makeText(ChatActivity.this, "Please Write Something", Toast.LENGTH_LONG).show();
                } else {
                    String messagetxt = binding.messageBox.getText().toString();
                    Date date = new Date();
                    Message message = new Message(messagetxt, senderUid, date.getTime());
                    binding.messageBox.setText("");

                    String baateKey = database.getReference().push().getKey();

                    HashMap<String, Object> lastMsgObj = new HashMap<>();
                    lastMsgObj.put("lastMsg", message.getMessage());
                    lastMsgObj.put("lastMsgTime", date.getTime());

                    database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                    database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsgObj);


                    database.getReference()
                            .child("Chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(baateKey)
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            database.getReference()
                                    .child("Chats")
                                    .child(receiverRoom)
                                    .child("messages")
                                    .child(baateKey)
                                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendNotification(name, message.getMessage(), token);
                                }
                            });


                        }
                    });


                }
            }
        });
        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderUid).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("Online");
                }
            };
        });


        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 25);

            }
        });


    }
    void sendNotification(String name , String message ,String token){
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", name);
            data.put("body", message);

            JSONObject notificationData = new JSONObject();
            notificationData.put("notification" , data);
            notificationData.put("to" , token);

            JsonObjectRequest request = new JsonObjectRequest(url, notificationData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                   // Toast.makeText(ChatActivity.this,"Success",Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(ChatActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String , String> map = new HashMap<>();
                   String key = "key=AAAAcXeYmzg:APA91bGYv15fqFzH1Axj6wibWg2k5wS5UpsFtBI_8Iw2dolKAgAZccWtgcTbFg0gxOisw8hiFJ2fW5LP3-f1t9HkiPcExh-w3Tcn0SdPwYAR0Mcmc75L1iQxsGJQ6S_NFPMl0jknxBn-";
                   map.put("Authorization" ,key);
                   map.put("Content-Type" , "application/json");
                    return map;
                }
            };

            queue.add(request);

        }catch (Exception exception){

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 25) {
            if (data != null) {
                if (data.getData() != null) {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        //Toast.makeText(ChatActivity.this ,filepath,Toast.LENGTH_LONG).show();
                                        String messagetxt = binding.messageBox.getText().toString();
                                        Date date = new Date();
                                        Message message = new Message(messagetxt, senderUid, date.getTime());
                                        message.setMessage("Photo");
                                        message.setImageUrl(filepath);
                                        binding.messageBox.setText("");
                                        String baateKey = database.getReference().push().getKey();

                                        HashMap<String, Object> lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg", message.getMessage());
                                        lastMsgObj.put("lastMsgTime", date.getTime());

                                        database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("Chats").child(receiverRoom).updateChildren(lastMsgObj);


                                        database.getReference()
                                                .child("Chats")
                                                .child(senderRoom)
                                                .child("messages")
                                                .child(baateKey)
                                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                database.getReference()
                                                        .child("Chats")
                                                        .child(receiverRoom)
                                                        .child("messages")
                                                        .child(baateKey)
                                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });


                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
        binding.status.setText("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
        binding.status.setText("Offline");
    }
}


