package com.example.gpstracking;

public class GetRoute {
    public double lat, lng;
    public String routeName;
    public  GetRoute(){}

    public GetRoute(double Routelat, double Routelong, String routeName) {
        this.lat = Routelat;
        this.lng = Routelong;
        this.routeName = routeName;

    }
}
