package com.example.android.cipherchat.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cipherchat.Objects.Message;
import com.example.android.encryptedmessengerapp.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<Message> messages = new ArrayList<>();
    private String username;

    public MessageAdapter(String username) {
        this.username = username;
    }

    public void add(Message message){
        messages.add(0, message);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSender().equals(username)) return VIEW_TYPE_MESSAGE_SENT;
        else return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_sent, viewGroup, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_message_received, viewGroup, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        String message = messages.get(i).getMessage();

        if (viewHolder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) ((SentMessageViewHolder) viewHolder).message.setText(message);
        else ((ReceivedMessageViewHolder) viewHolder).message.setText(message);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message_sent);
        }

    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        TextView message;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.tv_message_received);
        }
    }


}
