package com.example.lucy.p2pmessagingapp.TCPCommunication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Tomislav on 1/17/2018.
 */

public class TcpClient {
    private Socket clientSocket;
    private DataInputStream is;
    private DataOutputStream os;

    public void OpenConnection(String address, int port){
        //open new socket and i/o streams
        try {
            clientSocket = new Socket(address, port);
            is = new DataInputStream((clientSocket.getInputStream()));
            os = new DataOutputStream((clientSocket.getOutputStream()));
            System.out.println("konekcija otvorena");
        }catch (IOException ex){
            System.out.println("Cant open new socket: " + ex);
        }
    }

    public String GetResponse(String data){
        try{
            os.writeBytes(data);
            String response = is.readLine();
            System.out.println("Response: " + response);
            return response;
        }catch (IOException ex){
            System.out.println("Cant send data: " + ex);
            return null;

        }
    }

    public void closeConnection(){
        try {
            clientSocket.close();
            System.out.println("konekcija zatvorena");
        }catch (IOException ex){
            System.out.println("Cant close connection: " + ex);
        }
    }
}
