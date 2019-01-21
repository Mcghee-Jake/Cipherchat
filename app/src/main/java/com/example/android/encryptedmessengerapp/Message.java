package com.example.android.encryptedmessengerapp;

public class Message {

    private String message;

    public Message() {} // Required for firebase serialization

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
