package com.binary2.baate.Baate_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.binary2.baate.R;
import com.binary2.baate.databinding.ActivityOtpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;

public class OtpActivity extends AppCompatActivity {
    ActivityOtpBinding binding;
    FirebaseAuth auth;
    String verficationId;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP ....");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();
        String phonenumber = getIntent().getStringExtra("phonenumber");
        binding.phoneotp.setText("Verify  " + phonenumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phonenumber)
                .setTimeout(60L , TimeUnit.SECONDS)
                .setActivity(OtpActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String verifyId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken);
                        dialog.dismiss();
                        verficationId = verifyId;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verficationId , otp);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(OtpActivity.this,"Verification Successful",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(OtpActivity.this,SetupProfileActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(OtpActivity.this,"Verification Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }

}