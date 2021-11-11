package com.binary2.baate.Baate_Adapters;

import android.content.Context;
import android.media.session.MediaController;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.binary2.baate.Baate_Models.Message;
import com.binary2.baate.Baate_Services.FirebaseServices;
import com.binary2.baate.R;
import com.binary2.baate.databinding.ItemReceivedBinding;
import com.binary2.baate.databinding.ItemSentBinding;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    SimpleExoPlayer simpleExoPlayer;
    PlayerView playerView;
    String senderRoom;
    String receiverRoom;
    VideoView VideoView;



    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {

        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);


        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder viewHolder = (SentViewHolder) holder;
            if (message.getMessage().equals("Photo")) {
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.textView8.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.avatarforprofile)
                        .into(viewHolder.binding.image);

            }

            viewHolder.binding.textView8.setText(message.getMessage());




        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;

            if (message.getMessage().equals("Photo")){
                viewHolder.binding.image.setVisibility(View.VISIBLE);
                viewHolder.binding.textView7.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageUrl())
                        .placeholder(R.drawable.avatarforprofile)
                        .into(viewHolder.binding.image);
            }

            viewHolder.binding.textView7.setText(message.getMessage());


        }

    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceivedBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceivedBinding.bind(itemView);
        }
    }

}
