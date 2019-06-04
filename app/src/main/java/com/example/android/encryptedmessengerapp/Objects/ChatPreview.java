package com.example.android.encryptedmessengerapp.Objects;

public class ChatPreview {
    private String chatPartnerEmail;
    private String lastMessage;

    public ChatPreview(String chatPartnerEmail, String lastMessage) {
        this.chatPartnerEmail = chatPartnerEmail;
        this.lastMessage = lastMessage.substring(0, 48).trim() + "...";
    }

    public String getChatPartnerEmail() { return chatPartnerEmail; }
    public String getLastMessage() { return lastMessage; }


}
