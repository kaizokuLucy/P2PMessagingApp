package com.example.lucy.p2pmessagingapp.TCPCommunication;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Tomislav on 1/17/2018.
 */

public class TcpServer {
    private static final int NUMBER_OF_THREADS = 4;
    private static final int BACKLOG = 10;  //Max queue length for incoming connection requests.
    private final AtomicInteger activeConnections;
    private ServerSocket serverSocket;
    private final ExecutorService executor;
    private final AtomicBoolean runningFlag;
    private Context context;

    public TcpServer(Context context){
        activeConnections = new AtomicInteger(0);
        executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        runningFlag = new AtomicBoolean(false);
        this.context = context;
    }

    public void startup() {
        // create a server socket, bind it to the specified port
        // on the local host and set the max queue length for client requests
        try (ServerSocket serverSocket = new ServerSocket(52520, BACKLOG)) {
            this.serverSocket = serverSocket;
            // set timeout to avoid blocking
            serverSocket.setSoTimeout(500);
            setRunningFlag(true);
            Log.d("Server log", "Waiting for clients");
            loop();
            shutdown();
        } catch (IOException ex) {
            Log.d("Server log", "Exception caught when opening or setting the socket: " + ex);
        } finally {
            // CLOSE
            executor.shutdown();
        }
    }

    public void loop() {
        while(runningFlag.get()) {
            try{
                // create a new socket, accept and listen for a
                //connection to be made to this socket
                Socket clientSocket = serverSocket.accept();
                // execute a tcp request handler in a new thread
                Runnable worker = new Worker(clientSocket, runningFlag, activeConnections, context);
                executor.execute(worker);
                activeConnections.set(activeConnections.get() + 1);
            } catch(SocketTimeoutException ste) {
                // do nothing, check runningFlag flag
            } catch(IOException ex) {
                Log.d("Server log", "Exception caught when waiting for a connection: " + ex);
            }
        }

    }

    public void shutdown() {
        while (activeConnections.get() > 0) {
            Log.d("Server log", "WARNING: There are still active connections");
            try {
                Thread.sleep(5000);
            } catch (java.lang.InterruptedException e) {
            }
        }
        if (activeConnections.get() == 0) {
            Log.d("Server log", "Server shutdown.");

        }
    }

    public boolean getRunningFlag() {
        return runningFlag.get();
    }

    public void setRunningFlag(boolean flag) {
        runningFlag.set(flag);
    }

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    public String getIPAddress(){
        return serverSocket.getInetAddress().getHostAddress();
    }
}
