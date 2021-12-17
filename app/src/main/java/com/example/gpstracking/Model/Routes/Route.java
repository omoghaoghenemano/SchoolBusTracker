package com.example.gpstracking.Model.Routes;

public class Route {



    public String routeName;

   public  Double Lat, Lng;
   public  Route(){}

    public Route(String routename) {
        routeName = routename;


    }

    public String getName() {
        return routeName;
    }

    public void setName(String name) {
        routeName = name;
    }
}
