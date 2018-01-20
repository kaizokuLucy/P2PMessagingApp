package com.example.lucy.p2pmessagingapp.TCPCommunication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lucy.p2pmessagingapp.HomeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tomislav on 1/17/2018.
 */

public class Worker implements Runnable {
    private final Socket clientSocket;
    private final AtomicBoolean isRunning;
    private final AtomicInteger activeConnections;
    private HomeActivity context;

    public Worker(Socket clientSocket, AtomicBoolean isRunning, AtomicInteger activeConnections, Context context) {
        this.clientSocket = clientSocket;
        this.isRunning = isRunning;
        this.activeConnections = activeConnections;
        this.context = (HomeActivity) context;
    }

    @Override
    public void run() {
        try (//create a new BufferedReader from an existing InputStream
             BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             //create a PrintWriter from an existing OutputStream
             PrintWriter outToClient = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {

            String receivedString;
            boolean windowActive = false;
            // read a few lines of text
            while ((receivedString = inFromClient.readLine()) != null) {
                Log.d("Worker: ", "Server received:" + receivedString);
                //end connection
                if (receivedString.contains("end-connection")) {
                    activeConnections.set(activeConnections.get() - 1);
                    Log.d("Worker: ", "TCP konekcija je zatvorena");
                    return;
                }
                //shutdown the server if requested
                if (receivedString.contains("shutdown")) {
                    isRunning.set(false);
                    activeConnections.set(activeConnections.get() - 1);
                    return;
                }
                //start new chat window
                if (windowActive == false) {
                    context.createChat();
                    windowActive = true;

                }

                String stringToSend = "hello world";
                // send a String then terminate the line and flush
                outToClient.println(stringToSend);
                Log.d("Worker: ", "Server sent: " + stringToSend);
            }
            activeConnections.set(activeConnections.get() - 1);
        } catch (IOException ex) {
            Log.d("Worker: ", "Exception caught when trying to read or send data: " + ex);
        }
    }
}
