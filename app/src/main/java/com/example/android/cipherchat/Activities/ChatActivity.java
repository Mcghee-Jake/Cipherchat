package com.example.android.cipherchat.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.android.cipherchat.Adapters.MessageAdapter;
import com.example.android.cipherchat.Objects.Message;
import com.example.android.cipherchat.Utils.AESEncryptionHelper;
import com.example.android.cipherchat.Utils.MiscUtils;
import com.example.android.cipherchat.Utils.RSAEncyptionHelper;
import com.example.android.encryptedmessengerapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    private String user_id;
    private String chatID;
    private MessageAdapter messageAdapter;
    private String chatPartnerEmail;
    private String chatPartnerID;
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

        user_id = getIntent().getStringExtra("USERNAME");
        chatPartnerEmail = getIntent().getStringExtra("CHAT_PARTNER_EMAIL");

        if (chatPartnerEmail == null) startNewChat();// If this is a new conversation
        else setUpChat(); // Chat has been initialized
    }

    private void startNewChat() {
        hideProgressBar();
        newChatActionBar();
        hideMessaging();
    }

    private void setUpChat(){
        initializedChatActionBar();
        setupDatabaseListeners();
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
                chatPartnerEmail = etRecipient.getText().toString().toLowerCase().trim();

                // Check to make sure that the chat partner is a registered user
                firebaseDatabase.child("users").orderByChild("email").equalTo(chatPartnerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) setUpChat(); // If they are registered, set up the chat
                        else { // If they are not registered, show an error message
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                            builder.setMessage("No user found with this e-mail address")
                                    .setCancelable(true)
                                    .setTitle("Error")
                                    .setPositiveButton("OK", null);
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
    }


    private void initializedChatActionBar() {
        findViewById(R.id.toolbar_chat_activity_new).setVisibility(View.GONE);
        findViewById(R.id.toolbar_chat_activity_initiated).setVisibility(View.VISIBLE);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_chat_activity_initiated));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(chatPartnerEmail);
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
                    btnSend.setImageResource(R.drawable.ic_send_white_24dp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageString = etMessage.getText().toString().trim();
                if (!messageString.isEmpty()) { // If there is a valid string in the message field
                    etMessage.setText(""); // Clear the message field

                    // Encrypt the message with AES
                    final String keyAES = AESEncryptionHelper.generateKey();
                    final String encryptedMessage = AESEncryptionHelper.encrypt(messageString, keyAES);

                    // Get the sender's public key
                    firebaseDatabase.child("users").child(user_id).child("public_key").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final String senderPublicKey = dataSnapshot.getValue(String.class);

                            // Get the receiver's public key
                            firebaseDatabase.child("users").child(chatPartnerID).child("public_key").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String receiverPublicKey = dataSnapshot.getValue(String.class);

                                    // Encrypt the AES key with the pair of public keys
                                    String senderEncryptedKey = RSAEncyptionHelper.encrypt(keyAES, RSAEncyptionHelper.getPublicKeyFromString(senderPublicKey));
                                    String receiverEncryptedKey = RSAEncyptionHelper.encrypt(keyAES, RSAEncyptionHelper.getPublicKeyFromString(receiverPublicKey));

                                    // Put both keys into a HashMap so they can be included with the message
                                    HashMap<String, String> keyMap = new HashMap<>();
                                    keyMap.put(user_id, senderEncryptedKey);
                                    keyMap.put(chatPartnerID, receiverEncryptedKey);

                                    // Send the message through firebase
                                    Message message = new Message(user_id, encryptedMessage, keyMap);
                                    firebaseDatabase.child("chatMessages").child(chatID).push().setValue(message);
                                    firebaseDatabase.child("chatInfo").child(chatID).child("lastMessage").setValue(message);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });

                    // Add chat partner info to firebase database if this is the first message between the users
                    firebaseDatabase.child("userChats").child(user_id).child(chatPartnerID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                // Add the users to userChats
                                firebaseDatabase.child("userChats").child(user_id).child(chatPartnerID).setValue(true);
                                firebaseDatabase.child("userChats").child(chatPartnerID).child(user_id).setValue(true);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }
            }
        });
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.rv_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(user_id);
        recyclerView.setAdapter(messageAdapter);
    }


    private void setupDatabaseListeners(){

        firebaseDatabase.child("users").orderByChild("email").equalTo(chatPartnerEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    chatPartnerID = dataSnapshot.getChildren().iterator().next().getKey();
                    chatID = MiscUtils.getChatRoomID(user_id, chatPartnerID);

                    firebaseDatabase.child("chatMessages").child(chatID).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            hideProgressBar();
                            Message encryptedMessage = dataSnapshot.getValue(Message.class);
                            messageAdapter.addEncryptedMessage(encryptedMessage);
                            Message decryptedMessage = encryptedMessage.decryptMessage(user_id);
                            messageAdapter.addDecryptedMessage(decryptedMessage);
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
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    private void hideProgressBar(){
        findViewById(R.id.pb_chat_activity).setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_toggle_decryption) messageAdapter.toggleDecryption();
        return super.onOptionsItemSelected(item);
    }
}
