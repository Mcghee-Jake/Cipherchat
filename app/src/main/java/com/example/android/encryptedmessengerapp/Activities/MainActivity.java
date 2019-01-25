package com.example.android.encryptedmessengerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.encryptedmessengerapp.Adapters.ConversationAdapter;
import com.example.android.encryptedmessengerapp.Objects.Conversation;
import com.example.android.encryptedmessengerapp.Objects.Message;
import com.example.android.encryptedmessengerapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> activeChats;
    private String username = "USERNAME";
    private ConversationAdapter conversationAdapter;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();



        FloatingActionButton fabStartConversation = findViewById(R.id.fab_start_conversation);
        fabStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        activeChats = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("userChats").child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                activeChats.add(dataSnapshot.getKey());
                Log.d("TEST", dataSnapshot.getKey());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        for (String chatID : activeChats) {
            FirebaseDatabase.getInstance().getReference().child("chatInfo").child(chatID).child("members").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String members = dataSnapshot.getKey();
                    Log.d("TEST 2", dataSnapshot.getKey());
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_conversations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        conversationAdapter = new ConversationAdapter();
        recyclerView.setAdapter(conversationAdapter);
    }

}
