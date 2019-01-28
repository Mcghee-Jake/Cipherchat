package com.example.android.encryptedmessengerapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.encryptedmessengerapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {

    List<String> chatPartners = new ArrayList<>();
    List<String> chatPreviews = new ArrayList<>();

    public ChatRoomAdapter() {

    }

    public void update(List<String> chatPartners, List<String> chatPreviews) {
        this.chatPartners = chatPartners;
        this.chatPreviews = chatPreviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_item, viewGroup, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder chatViewHolder, int i) {
        chatViewHolder.chatPartner.setText(chatPartners.get(i));
        chatViewHolder.chatPreview.setText(chatPreviews.get(i));
    }

    @Override
    public int getItemCount() {
        return chatPartners.size();
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        TextView chatPartner, chatPreview;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            chatPartner = itemView.findViewById(R.id.tv_chat_partner);
            chatPreview = itemView.findViewById(R.id.tv_chat_preview);
        }

    }

}
