package com.example.lucy.p2pmessagingapp.TCPCommunication;

/**
 * Created by Tomislav on 1/17/2018.
 */

public class TcpServerThread implements Runnable{
    private TcpServer server;

    public TcpServerThread(TcpServer server){
        this.server = server;
    }

    @Override
    public void run() {
        server.startup();
    }
}
