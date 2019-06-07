package com.example.android.cipherchat.Objects;

public class Message {

    private String message;
    private String sender;

    public Message() {} // Required for firebase serialization

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}
