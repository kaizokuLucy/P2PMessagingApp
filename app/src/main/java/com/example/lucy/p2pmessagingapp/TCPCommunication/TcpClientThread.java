package com.example.lucy.p2pmessagingapp.TCPCommunication;

/**
 * Created by Tomislav on 1/17/2018.
 */

public class TcpClientThread implements Runnable {

    private TcpClient client;

    public TcpClientThread(TcpClient client){
        this.client = client;
    }

    @Override
    public void run() {
        client.OpenConnection("192.168.1.3", 52520);
        String odgovor = client.GetResponse("Hello world nigga\n");
        odgovor.trim();
    }
}
