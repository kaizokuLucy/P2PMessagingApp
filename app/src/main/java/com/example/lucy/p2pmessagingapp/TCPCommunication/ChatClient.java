package com.example.lucy.p2pmessagingapp.TCPCommunication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by lucy on 1/18/18.
 */

public class ChatClient {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String line;
    private String clientIP;
    private int clientPort;
    private BufferedReader inFromClient;
    private DataOutputStream outToClient;

    public ChatClient(String IP, int port) {
        clientIP = IP;
        clientPort = port;
    }

    public void run() {
        while (true) {
            try {
//            clientSocket = new Socket(clientIP, clientPort);
                clientSocket = new Socket(clientIP, 54321);
//            inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToClient = new DataOutputStream(clientSocket.getOutputStream());
                Log.d("KLIJENT: ", "" + clientSocket.isConnected());
            } catch (IOException e) {
                Log.e("CLIENT RUN EXC", e.getMessage());
            }
            try {
                clientSocket.isConnected();
                break;
            } catch (NullPointerException e) {
                Log.e("NULL PTR", e.getMessage());
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        outToClient.writeBytes(msg);
        outToClient.writeBytes("\n");
        outToClient.flush();
        Log.d("MSG", "successfully sent");
    }
}
