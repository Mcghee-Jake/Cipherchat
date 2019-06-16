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

    private List<Message> encryptedMessages, decryptedMessages;
    private String user_id;
    private boolean showDecrypted;


    public MessageAdapter(String user_id) {
        this.user_id = user_id;
        encryptedMessages = new ArrayList<>();
        decryptedMessages = new ArrayList<>();
        showDecrypted = true;
    }

    public void addEncryptedMessage(Message message) {
        encryptedMessages.add(0, message);
        notifyDataSetChanged();
    }

    public void addDecryptedMessage(Message message) {
        decryptedMessages.add(0, message);
        notifyDataSetChanged();
    }

    public void toggleDecryption() {
        showDecrypted = !showDecrypted;
        notifyDataSetChanged();
    }
    

    @Override
    public int getItemViewType(int position) {
        List<Message> messages;
        if (showDecrypted) messages = decryptedMessages;
        else messages = encryptedMessages;

        if (messageWasSentByUser(messages, position)) return VIEW_TYPE_MESSAGE_SENT;
        else return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    private boolean messageWasSentByUser(List<Message> messages, int position) {
        if (messages.get(position).getSender().equals(user_id)) return true;
        else return false;
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
        String messageString;
        if (showDecrypted) messageString = decryptedMessages.get(i).getMessageString();
        else messageString = encryptedMessages.get(i).getMessageString();

        if (viewHolder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) ((SentMessageViewHolder) viewHolder).message.setText(messageString);
        else ((ReceivedMessageViewHolder) viewHolder).message.setText(messageString);

    }

    @Override
    public int getItemCount() {
        return encryptedMessages.size();
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
