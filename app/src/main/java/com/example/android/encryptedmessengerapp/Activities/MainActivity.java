package com.example.android.encryptedmessengerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.encryptedmessengerapp.Adapters.ChatRoomAdapter;
import com.example.android.encryptedmessengerapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ChatRoomAdapter.ChatInfoClickListener {

    private final int RC_SIGN_IN = 1;
    private String user_id;
    private ChatRoomAdapter chatRoomAdapter;
    private DatabaseReference firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ValueEventListener valueEventListener;

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
        if (authStateListener != null) { firebaseAuth.removeAuthStateListener(authStateListener);
        }
        chatRoomAdapter.clear();
        if (valueEventListener != null) {
            firebaseDatabase.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
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
                if (user != null) {
                    // User is signed in
                    user_id = user.getUid();
                    setupDatabase();

                } else {
                    // User is not signed in
                    user_id = null;
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

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_conversations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatRoomAdapter = new ChatRoomAdapter(this);
        recyclerView.setAdapter(chatRoomAdapter);
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

    private void setupDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        if (valueEventListener == null) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // For each chat partner
                    Log.i("info", "key; " + dataSnapshot.getKey() + " - value: " + dataSnapshot.getValue());
                /*
                if (chatMap != null) {
                    final List<String> chatPartners = new ArrayList<>(chatMap.keySet());
                    for (int i = 0; i < chatPartners.size(); i++) {
                        final String chatPartner = chatPartners.get(i);

                        final String chatRoomID = Utils.getChatRoomID(user_id, chatPartner);
                        Log.i("Chat Room ID: ", chatRoomID);
                        if (valueEventListener == null) {
                            valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String lastMessage = dataSnapshot.getValue(String.class);
                                    chatRoomAdapter.update(chatPartner, lastMessage);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            };
                            firebaseDatabase.child("chatInfo").child(chatRoomID).child("lastMessage").addListenerForSingleValueEvent(valueEventListener);
                        }
                    }
                }
                */
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
        }

        firebaseDatabase.child("userChats").child(user_id).addValueEventListener(valueEventListener);
    }

    @Override
    public void onChatInfoClicked(String chatPartner) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("USERNAME", user_id);
        intent.putExtra("CHAT_PARTNER", chatPartner);
        startActivity(intent);
    }
}
