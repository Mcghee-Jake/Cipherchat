package com.example.android.encryptedmessengerapp.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.encryptedmessengerapp.Adapters.MessageAdapter;
import com.example.android.encryptedmessengerapp.Objects.Message;
import com.example.android.encryptedmessengerapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private String username = "USERNAME";
    private boolean chatInitialized = false;
    private MessageAdapter messageAdapter;
    private ChildEventListener childEventListener;
    private String chatPartner;
    private String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!chatInitialized) { // If this is a new conversation
            getActionBar().setCustomView(R.layout.new_chat_actionbar);
            final EditText etRecipient = findViewById(R.id.et_recipient);
            ImageButton confirmRecipient = findViewById(R.id.btn_confirm_recipient);
            confirmRecipient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create a chatID in the form of  [lowerUserName_higherUserName]
                    chatPartner = etRecipient.getText().toString();
                    if (chatPartner.compareTo(username) < 0) chatID = chatPartner + "_" + username;
                    else chatID = username + "_" + chatPartner;

                    // Add the chat to the database
                    FirebaseDatabase.getInstance().getReference().child("chats");

                    // Add the users to the chat rooms
                    FirebaseDatabase.getInstance().getReference().child("chatRooms").child(username).child(chatID).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("chatRooms").child(chatPartner).child(chatID).setValue(true);

                    chatInitialized = true;
                }
            });
        } else { // Chat has been initialized
            getActionBar().setDisplayShowCustomEnabled(false);
            getActionBar().setTitle(chatPartner);


            setupRecyclerView();

            final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("chats").child(chatID);

            final EditText etMessage = findViewById(R.id.et_message);
            ImageButton btnSend = findViewById(R.id.btn_send);
            btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message(etMessage.getText().toString());
                    firebaseDatabase.push().setValue(message);
                    etMessage.setText("");
                }
            });

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageAdapter.add(message);
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            firebaseDatabase.addChildEventListener(childEventListener);

        }



    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
    }

}
