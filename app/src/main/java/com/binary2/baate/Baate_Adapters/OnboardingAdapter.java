package com.binary2.baate.Baate_Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binary2.baate.Baate_Models.OnboardingItem;
import com.binary2.baate.R;

import java.util.List;

public class OnboardingAdapter extends  RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>{
  private List<OnboardingItem> onboardingItems;

    public OnboardingAdapter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }


    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_onboarding , parent , false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
       holder.setOnboardingData(onboardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        private TextView textDescription;
        private ImageView imageview;

         OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageview = itemView.findViewById(R.id.imageOnboarding);
        }
        void setOnboardingData(OnboardingItem onboardingItem){
                textTitle.setText(onboardingItem.getTitle());
                textDescription.setText(onboardingItem.getDescription());
                imageview.setImageResource(onboardingItem.getImages());
        }
    }
}
