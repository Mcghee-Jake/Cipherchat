package com.example.android.encryptedmessengerapp.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.encryptedmessengerapp.Objects.Conversation;
import com.example.android.encryptedmessengerapp.R;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    List<Conversation> conversationList = new ArrayList<>();
    String username;

    public ConversationAdapter() {

    }

    public void add(Conversation conversation) {
        conversationList.add(conversation);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list_item, viewGroup, false);
        return new ConversationAdapter.ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder conversationViewHolder, int i) {
        Conversation conversation = conversationList.get(i);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        TextView conversationPartner;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            conversationPartner = itemView.findViewById(R.id.tv_conversation_partner);
        }

    }
}
