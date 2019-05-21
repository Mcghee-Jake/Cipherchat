package com.example.android.encryptedmessengerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.encryptedmessengerapp.Adapters.chatRoomRecyclerViewAdapter;
import com.example.android.encryptedmessengerapp.Objects.ChatPreview;
import com.example.android.encryptedmessengerapp.R;
import com.example.android.encryptedmessengerapp.Utils;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements chatRoomRecyclerViewAdapter.ChatInfoClickListener {

    private final int RC_SIGN_IN = 1;
    private String user_id;
    private chatRoomRecyclerViewAdapter chatRoomRecyclerViewAdapter;
    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ValueEventListener chatPartnersValueEventListener;
    private List<ValueEventListener> activeChatsValueEventListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authorizeUser();
        setupRecyclerView();
        setupFloatingActionButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Clear the authStateListener
        if (authStateListener != null) { firebaseAuth.removeAuthStateListener(authStateListener); }

        // Clear the value event listeners
        if (chatPartnersValueEventListener != null) {
            firebaseDatabase.removeEventListener(chatPartnersValueEventListener);
            chatPartnersValueEventListener = null;
        }
        if (activeChatsValueEventListeners != null) {
            for (ValueEventListener valueEventListener : activeChatsValueEventListeners) {
                firebaseDatabase.removeEventListener(valueEventListener);
            }
            activeChatsValueEventListeners = null;
        }


        // Clear the recyclerView
        chatRoomRecyclerViewAdapter.clear();
    }


    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void authorizeUser() {

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Check if user is logged in
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) { // If user is signed in
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                    setUserData(user);
                    getChatRoomData(); // Retrieve the data for that user
                } else { // If user is not signed in
                    user_id = null;
                    // Activate firebase login page
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void setUserData(final FirebaseUser user) {
        user_id = user.getUid(); // Get user ID
        firebaseDatabase.child("users").child(user_id).child("user_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    firebaseDatabase.child("users").child(user_id).child("user_id").setValue(user_id);
                    firebaseDatabase.child("users").child(user_id).child("email").setValue(user.getEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_conversations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatRoomRecyclerViewAdapter = new chatRoomRecyclerViewAdapter(this);
        recyclerView.setAdapter(chatRoomRecyclerViewAdapter);
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fabStartConversation = findViewById(R.id.fab_start_conversation);
        fabStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("USERNAME", user_id);
                startActivity(intent);
            }
        });
    }

    private void getChatRoomData() {
        if (chatPartnersValueEventListener == null) {
            chatPartnersValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Retrieve the list of people that the current user has active chats with
                    HashMap<String, Boolean> chatMap = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    if (chatMap != null) { // If the current user has active chats
                        final List<String> chatPartners = new ArrayList<>(chatMap.keySet());
                        activeChatsValueEventListeners = new ArrayList<>();
                        for (int i = 0; i < chatPartners.size(); i++) { // For each chat partner
                            final String chatPartnerID = chatPartners.get(i);
                            final String chatRoomID = Utils.getChatRoomID(user_id, chatPartnerID); // Get the id of the chat room
                            // Get the email of the chat partner
                            firebaseDatabase.child("users").child(chatPartnerID).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String chatPartnerEmail = dataSnapshot.getValue().toString();
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String lastMessage = dataSnapshot.getValue(String.class); // Retrieve the last message in the conversation
                                            ChatPreview chatPreview = new ChatPreview(chatPartnerEmail, lastMessage);
                                            chatRoomRecyclerViewAdapter.update(chatPreview); // Display the chat info in the recyclerView
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                                    };
                                    activeChatsValueEventListeners.add(valueEventListener);
                                    firebaseDatabase.child("chatInfo").child(chatRoomID).child("lastMessage").addListenerForSingleValueEvent(valueEventListener);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
        }

        firebaseDatabase.child("userChats").child(user_id).addValueEventListener(chatPartnersValueEventListener);
    }

    @Override
    public void onChatInfoClicked(ChatPreview chatPreview) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("USERNAME", user_id);
        intent.putExtra("CHAT_PARTNER_EMAIL", chatPreview.getChatPartnerEmail());
        startActivity(intent);
    }
}
