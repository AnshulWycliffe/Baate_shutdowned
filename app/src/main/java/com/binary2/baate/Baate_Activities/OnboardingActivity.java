package com.binary2.baate.Baate_Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.binary2.baate.Baate_Adapters.OnboardingAdapter;
import com.binary2.baate.Baate_Models.OnboardingItem;
import com.binary2.baate.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout  layoutOnboardingindicators;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);




        layoutOnboardingindicators = findViewById(R.id.layoutOnboardingIndicators);
        button = findViewById(R.id.buttonOnboardingAction);
        setupOnboardingItems();
        setLayoutOnboardingindicators();
        setCurrentOnboardingIndicator(0);



        ViewPager2 onboardingView = findViewById(R.id.OnboardingviewPager);
        onboardingView.setAdapter(onboardingAdapter);



        onboardingView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onboardingView.getCurrentItem()+1 < onboardingAdapter.getItemCount()){
                    onboardingView.setCurrentItem(onboardingView.getCurrentItem()+1);
                }else{
                    startActivity(new Intent(OnboardingActivity.this, PhoneNumberActivity.class));
                    finishAffinity();
                }
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();
        OnboardingItem welcome = new OnboardingItem();
        welcome.setTitle("Welcome");
        welcome.setDescription("Hey, Thank you for installing Baate , and trusting us for you safer way to communicate");
        welcome.setImages(R.drawable.baate_image_welcome);

        OnboardingItem easy = new OnboardingItem();
        easy.setTitle("Easy To Use Interface");
        easy.setDescription("Baate has a easy to use interface , so you can't get confused.");
        easy.setImages(R.drawable.baate_image_easy);

        OnboardingItem secure = new OnboardingItem();
        secure.setTitle("Encryption");
        secure.setDescription("Baate uses firebase default encryption method with https and save in our firebase database server , actually firebase is a product of Google ");
        secure.setImages(R.drawable.baate_image_encryption);

        OnboardingItem talk = new OnboardingItem();
        talk.setTitle("Connect With Loved Ones");
        talk.setDescription("Baate does not share your data with any third party person or individual or any service provider ");
        talk.setImages(R.drawable.baate_image_connect);

        onboardingItems.add(welcome);
        onboardingItems.add(easy);
        onboardingItems.add(secure);
        onboardingItems.add(talk);




        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }
    private  void  setLayoutOnboardingindicators(){
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for(int i = 0; i< indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingindicators.addView(indicators[i]);
        }
    }
    private  void  setCurrentOnboardingIndicator( int index){
        int childCount = layoutOnboardingindicators.getChildCount();
        for (int i = 0 ; i < childCount; i++){
            ImageView imageView = (ImageView)layoutOnboardingindicators.getChildAt(i);
            if(i == index ){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext() , R.drawable.onboarding_indicator_active)
                );
            }else{
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext() , R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if(index == onboardingAdapter.getItemCount()-1){
            button.setText("Finish");
        }else{
            button.setText("Next");
        }
    }



    }


