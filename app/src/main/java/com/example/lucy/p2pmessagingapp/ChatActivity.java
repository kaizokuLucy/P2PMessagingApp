package com.example.lucy.p2pmessagingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.Handler;

import com.example.lucy.p2pmessagingapp.Models.ChatMessage;
import com.example.lucy.p2pmessagingapp.Models.Contact;
import com.example.lucy.p2pmessagingapp.Models.MessageAdapter;
import com.example.lucy.p2pmessagingapp.TCPCommunication.ChatClient;
import com.example.lucy.p2pmessagingapp.TCPCommunication.ChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private ListView messagingListView;
    private List<ChatMessage> messagesList = new ArrayList<>();
    private Button sendButton;
    private EditText messageText;
    private ArrayAdapter<ChatMessage> adapter;
    private Contact contact;
    private String userNumber;
    private ChatServer chatServer;
    private ChatClient chatClient;
    private int port;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle data = getIntent().getExtras();

        contact = (Contact) getIntent().getSerializableExtra("Contact");
        setTitle(contact.getFirstName());

        messagingListView = (ListView) findViewById(R.id.messagingListView);
        sendButton = (Button) findViewById(R.id.sendButton);
        messageText = (EditText) findViewById(R.id.messageText);

        // set adapter for the list view
        adapter = new MessageAdapter(this, R.layout.left, messagesList);
        messagingListView.setAdapter(adapter);

        userNumber = data.getString("userNumber");
        port = Integer.parseInt(data.getString("port"));

        startConversation();
    }


    public void startConversation() {
        chatServer = new ChatServer(port, messagesList, adapter);
        chatClient = new ChatClient(contact.getIp(), port);


        new Thread(new Runnable() {
            @Override
            public void run() {
                chatServer.run();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                chatClient.run();
            }
        }).start();
    }


    public void sendMessage(View view) {
        final String msg = messageText.getText().toString();
        Log.d("MSG:", msg);
        if (msg.equals(""))
            return;

        handler.post(new Runnable() {
            @Override
            public void run() {
                messagesList.add(new ChatMessage(msg, true));
                adapter.notifyDataSetChanged();
                messageText.setText("");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatClient.sendMessage(msg);
                } catch (IOException e) {
                    Log.e("SEND ERROR", e.getMessage());
                }
            }
        }).start();

    }
}
