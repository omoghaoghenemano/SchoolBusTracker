package com.example.gpstracking;

public class DriverInfo {

    public double lat;
    public double lng;
    public String name;
    public String vehiclenumber;
    public String Email;
    public String Password;

    public  DriverInfo(){

    }

    public DriverInfo(String email, double lat, double lng, String name, String password, String vehiclenumber) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.vehiclenumber = vehiclenumber;
        this.Email = email;
        this.Password = password;
    }

    }