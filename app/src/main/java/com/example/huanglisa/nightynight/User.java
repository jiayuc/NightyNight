package com.example.huanglisa.nightynight;

/**
 * Created by huanglisa on 11/13/16.
 */

public class User {
    public String name;
    public String email;
    public String address;
    public String phone;
    public String token;
    public String password;
    public boolean status;

    public User(String email, String password, String name, String address, String phone, boolean status){
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.token = null;
        this.status = status;
    }

    public void setToken(String token){
        this.token = token;
    }
}
