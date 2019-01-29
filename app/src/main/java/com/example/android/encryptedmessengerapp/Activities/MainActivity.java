package com.example.android.encryptedmessengerapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.encryptedmessengerapp.Adapters.ChatRoomAdapter;
import com.example.android.encryptedmessengerapp.R;
import com.example.android.encryptedmessengerapp.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ChatRoomAdapter.ChatInfoClickListener {

    private String username = "User1";
    private ChatRoomAdapter chatRoomAdapter;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        setupFloatingActionButton();
        setupDatabase();

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
                startActivity(intent);
            }
        });
    }

    private void setupDatabase() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase.child("userChats").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> chatMap = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if (chatMap != null) {
                    final List<String> chatPartners = new ArrayList<>(chatMap.keySet());
                    for (int i = 0; i < chatPartners.size(); i++) {
                        final String chatPartner = chatPartners.get(i);
                        final String chatRoomID = Utils.getChatRoomID(username, chatPartner);
                        firebaseDatabase.child("chatInfo").child(chatRoomID).child("lastMessage").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String lastMessage = dataSnapshot.getValue(String.class);
                                chatRoomAdapter.update(chatPartner, lastMessage);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    @Override
    public void onChatInfoClicked(String chatPartner) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("CHAT_PARTNER", chatPartner);
        startActivity(intent);
    }
}
