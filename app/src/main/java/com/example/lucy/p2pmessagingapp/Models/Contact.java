package com.example.lucy.p2pmessagingapp.Models;

import java.io.Serializable;

/**
 * Created by Tomislav on 1/13/2018.
 */

public class Contact implements Serializable {

    private String firstName;
    private String lastName;
    private String number;
    private String ip;
    private String status;

    public Contact(String first_name, String last_name, String number, String ip, String status){
        this.firstName = first_name;
        this.lastName = last_name;
        this.number = number;
        this.ip = ip;
        this.status = status;
    }

    public Contact(){
        super();
    }

    public String getIp() {
        return ip;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }
}