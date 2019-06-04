package com.example.android.encryptedmessengerapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.encryptedmessengerapp.Objects.ChatPreview;
import com.example.android.encryptedmessengerapp.R;

import java.util.ArrayList;
import java.util.List;

public class chatRoomRecyclerViewAdapter extends RecyclerView.Adapter<chatRoomRecyclerViewAdapter.ChatRoomViewHolder> {

    private List<ChatPreview> chatPreviews = new ArrayList<>();

    private ChatInfoClickListener chatInfoClickListener;

    public chatRoomRecyclerViewAdapter(ChatInfoClickListener chatInfoClickListener) {
        this.chatInfoClickListener = chatInfoClickListener;
    }

    public interface ChatInfoClickListener {
        void onChatInfoClicked(ChatPreview chatPreview);
    }

    public void clear() {
        this.chatPreviews = new ArrayList<>();
    }

    public void update(ChatPreview chatPreview) {
        this.chatPreviews.add(chatPreview);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_preview_list_item, viewGroup, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder chatViewHolder, int i) {
        ChatPreview chatPreview = chatPreviews.get(i);
        chatViewHolder.chatPartner.setText(chatPreview.getChatPartnerEmail());
        chatViewHolder.chatPreview.setText(chatPreview.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return chatPreviews.size();
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
            chatInfoClickListener.onChatInfoClicked(chatPreviews.get(getAdapterPosition()));
        }
    }

}
