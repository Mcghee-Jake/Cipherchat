package com.example.android.cipherchat.Objects;

public class ChatPreview {
    private String chatPartnerEmail;
    private String lastMessage;

    public ChatPreview(String chatPartnerEmail, String lastMessage) {
        this.chatPartnerEmail = chatPartnerEmail;
        if (lastMessage.length() <= 36) this.lastMessage = lastMessage;
        else this.lastMessage = lastMessage.substring(0, 36).trim() + "...";
    }

    public String getChatPartnerEmail() { return chatPartnerEmail; }
    public String getLastMessage() { return lastMessage; }


}
