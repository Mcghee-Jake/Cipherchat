package com.example.android.encryptedmessengerapp;

public class Utils {

    /**
     * Create a chatID in the form of  [lowerUserName_higherUserName]
     *
     * @param user1
     * @param user2
     * @return
     */
    public static String getChatRoomID(String user1, String user2) {
        if (user1.compareTo(user2) < 0)  return user1 + "_" + user2;
        else return user2 + "_" + user1;
    }
}
