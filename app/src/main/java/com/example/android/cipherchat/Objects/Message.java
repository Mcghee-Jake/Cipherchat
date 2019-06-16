package com.example.android.cipherchat.Objects;

import com.example.android.cipherchat.Utils.AESEncryptionHelper;
import com.example.android.cipherchat.Utils.RSAEncyptionHelper;

import java.security.PrivateKey;
import java.util.HashMap;

public class Message {

    private String sender;
    private String messageString;
    private HashMap<String, String> keys;

    public Message() {} // Required for firebase serialization

    public Message(String sender, String messageString, HashMap<String, String> keys) {
        this.sender = sender;
        this.messageString = messageString;
        this.keys = keys;
    }

    public Message decryptMessage(String user_id) {
        PrivateKey privateKey = RSAEncyptionHelper.getPrivateKey(user_id);
        String encryptedAESkey = keys.get(user_id);
        String decryptedAESKey = RSAEncyptionHelper.decrypt(encryptedAESkey, privateKey);
        String decryptedMessage = AESEncryptionHelper.decrypt(messageString, decryptedAESKey);
        return new Message(sender, decryptedMessage, keys);
    }

    public String getMessageString() {
        return messageString;
    }

    public String getSender() {
        return sender;
    }

    public HashMap<String, String> getKeys() { return keys; }



}
