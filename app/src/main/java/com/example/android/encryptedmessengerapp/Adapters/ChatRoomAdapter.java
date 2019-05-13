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

    private ChatInfoClickListener chatInfoClickListener;

    public ChatRoomAdapter(ChatInfoClickListener chatInfoClickListener) {
        this.chatInfoClickListener = chatInfoClickListener;
    }

    public interface ChatInfoClickListener {
        void onChatInfoClicked(String chatPartner);
    }

    public void clear() {
        this.chatPartners = new ArrayList<>();
        this.chatPreviews = new ArrayList<>();
    }

    public void update(String chatPartner, String chatPreview) {
        this.chatPartners.add(chatPartner);
        this.chatPreviews.add(chatPreview);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_info_list_item, viewGroup, false);
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

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView chatPartner, chatPreview;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            chatPartner = itemView.findViewById(R.id.tv_chat_partner);
            chatPreview = itemView.findViewById(R.id.tv_chat_preview);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            chatInfoClickListener.onChatInfoClicked(chatPartners.get(getAdapterPosition()));
        }
    }

}
