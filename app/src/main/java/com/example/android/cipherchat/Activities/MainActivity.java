package com.example.android.cipherchat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.cipherchat.Adapters.chatRoomRecyclerViewAdapter;
import com.example.android.cipherchat.Objects.ChatPreview;
import com.example.android.cipherchat.Objects.Message;
import com.example.android.cipherchat.Utils.MiscUtils;
import com.example.android.cipherchat.Utils.RSAEncyptionHelper;
import com.example.android.encryptedmessengerapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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


        initializeToolbar();
        authorizeUser();
        setupRecyclerView();
        setupFloatingActionButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Clear the authStateListener
        if (authStateListener != null) { firebaseAuth.removeAuthStateListener(authStateListener); }

        clearData();
    }

    private void clearData() {
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

    private void initializeToolbar() {
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_main_activity));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.btn_tutorial:
                // Launch the tutorial activity
                Intent intent = new Intent(MainActivity.this, TutorialMenuActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_logout:
                // Logout of firebase
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                clearData();
                            }
                        });
                break;
        }


        return super.onOptionsItemSelected(item);
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
                                    .setLogo(R.mipmap.ic_launcher)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
                chatRoomRecyclerViewAdapter.clear();
            }
        };
    }

    private void setUserData(final FirebaseUser user) {
        user_id = user.getUid(); // Get user ID
        firebaseDatabase.child("users").child(user_id).child("user_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    KeyPair keyPair = RSAEncyptionHelper.generateKeys(MainActivity.this, user_id);
                    String publicKeyString = RSAEncyptionHelper.convertPublicKeyToString(keyPair);

                    firebaseDatabase.child("users").child(user_id).child("user_id").setValue(user_id);
                    firebaseDatabase.child("users").child(user_id).child("email").setValue(user.getEmail());
                    firebaseDatabase.child("users").child(user_id).child("public_key").setValue(publicKeyString);
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
        chatRoomRecyclerViewAdapter.clear();
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
                    // Hide the progress bar
                    findViewById(R.id.pb_main_activity).setVisibility(View.GONE);

                    // Clear the recyclerView
                    chatRoomRecyclerViewAdapter.clear();

                    // Retrieve the list of people that the current user has active chats with
                    HashMap<String, Boolean> chatMap = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    if (chatMap != null) { // If the current user has active chats
                        findViewById(R.id.tv_chat_room_empty_state).setVisibility(View.GONE);

                        final List<String> chatPartners = new ArrayList<>(chatMap.keySet());
                        activeChatsValueEventListeners = new ArrayList<>();
                        for (int i = 0; i < chatPartners.size(); i++) { // For each chat partner
                            final String chatPartnerID = chatPartners.get(i);
                            final String chatRoomID = MiscUtils.getChatRoomID(user_id, chatPartnerID); // Get the id of the chat room
                            // Get the email of the chat partner
                            firebaseDatabase.child("users").child(chatPartnerID).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    final String chatPartnerEmail = dataSnapshot.getValue().toString();
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Message encryptedMessage = dataSnapshot.getValue(Message.class); // Retrieve the last message in the conversation
                                            if (encryptedMessage != null) {
                                                String decryptedMessage =  encryptedMessage.decryptMessage(user_id).getMessageString(); // Decrypt the message
                                                ChatPreview chatPreview = new ChatPreview(chatPartnerEmail, decryptedMessage);
                                                chatRoomRecyclerViewAdapter.update(chatPreview); // Display the chat info in the recyclerView
                                            }
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
                    } else { // User does not have any active chats
                        findViewById(R.id.tv_chat_room_empty_state).setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
        }

        firebaseDatabase.child("userChats").child(user_id).addValueEventListener(chatPartnersValueEventListener);
    }

    private void testEncryption() {

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("public_key").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String publicKeyString = dataSnapshot.getValue(String.class);
                PublicKey publicKey = RSAEncyptionHelper.getPublicKeyFromString(publicKeyString);
                PrivateKey privateKey = RSAEncyptionHelper.getPrivateKey(user_id);

                String secretMessage = "This is soooo secret!";
                Log.d("XYZ", "Secret Message - " + secretMessage);
                String encryptedMessage = RSAEncyptionHelper.encrypt(secretMessage, publicKey);
                Log.d("XYZ", "Encrypted Message - " + encryptedMessage);
                String decryptedMessage = RSAEncyptionHelper.decrypt(encryptedMessage, privateKey);
                Log.d("XYZ", "Decrypted Message - " + decryptedMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onChatInfoClicked(ChatPreview chatPreview) {
        Log.d("XYZ", "user id 2 -" + user_id);

        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("USERNAME", user_id);
        intent.putExtra("CHAT_PARTNER_EMAIL", chatPreview.getChatPartnerEmail());
        startActivity(intent);
    }

}
