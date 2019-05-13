package com.example.android.encryptedmessengerapp.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.encryptedmessengerapp.Adapters.MessageAdapter;
import com.example.android.encryptedmessengerapp.Objects.Message;
import com.example.android.encryptedmessengerapp.R;
import com.example.android.encryptedmessengerapp.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private String username;
    private String chatID;
    private MessageAdapter messageAdapter;
    private String chatPartner;
    private EditText etMessage;
    private ImageButton btnSend;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        username = getIntent().getStringExtra("USERNAME");
        chatPartner = getIntent().getStringExtra("CHAT_PARTNER");

        if (chatPartner == null) startNewChat();// If this is a new conversation
        else setUpChat(); // Chat has been initialized
    }

    private void startNewChat() {
        newChatActionBar();
        hideMessaging();
    }

    private void setUpChat(){
        initializedChatActionBar();
        setupDatabaseListener();
        showMessaging();
        setupRecyclerView();
    }

    private void newChatActionBar() {

        findViewById(R.id.toolbar_chat_activity_initiated).setVisibility(View.GONE);
        findViewById(R.id.toolbar_chat_activity_new).setVisibility(View.VISIBLE);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_chat_activity_new));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText etRecipient = findViewById(R.id.et_recipient);
        ImageButton confirmRecipient = findViewById(R.id.btn_confirm_recipient);
        confirmRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPartner = etRecipient.getText().toString();
                chatID = Utils.getChatRoomID(username, chatPartner);

                // Add the users to userChats
                firebaseDatabase.child("userChats").child(username).child(chatPartner).setValue(true);
                firebaseDatabase.child("userChats").child(chatPartner).child(username).setValue(true);

                // Initialize the chat
                setUpChat();
            }
        });
    }

    private void initializedChatActionBar() {
        findViewById(R.id.toolbar_chat_activity_new).setVisibility(View.GONE);
        findViewById(R.id.toolbar_chat_activity_initiated).setVisibility(View.VISIBLE);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_chat_activity_initiated));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(chatPartner);
    }

    private void hideMessaging(){
        etMessage.setVisibility(View.GONE);
        btnSend.setVisibility(View.GONE);
    }

    private void showMessaging(){
        etMessage.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.VISIBLE);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etMessage.getText().toString().equals("")) {
                    btnSend.setEnabled(false);
                    btnSend.setImageResource(R.drawable.ic_send_grey_24dp);
                } else {
                    btnSend.setEnabled(true);
                    btnSend.setImageResource(R.drawable.ic_send_accent_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(username, etMessage.getText().toString());
                firebaseDatabase.child("chatMessages").child(chatID).push().setValue(message);
                firebaseDatabase.child("chatInfo").child(chatID).child("lastMessage").setValue(message.getMessage());
                etMessage.setText("");
            }
        });
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(username);
        recyclerView.setAdapter(messageAdapter);
    }

    private void setupDatabaseListener(){
        chatID = Utils.getChatRoomID(username, chatPartner);

        firebaseDatabase.child("chatMessages").child(chatID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageAdapter.add(message);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

}
