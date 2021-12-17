package com.example.gpstracking;

public class Methods {
    private String RouteName;
    private Double Lat,Lng;
    public  Methods(){

    }

    public Methods(String routeName, Double lat, Double lng) {
        RouteName = routeName;
        Lat = lat;
        Lng = lng;
    }

    public String getRouteName() {
        return RouteName;
    }

    public void setRouteName(String routeName) {
        RouteName = routeName;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLng() {
        return Lng;
    }

    public void setLng(Double lng) {
        Lng = lng;
    }
}
