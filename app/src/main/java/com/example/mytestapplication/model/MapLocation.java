package com.example.mytestapplication.model;

import com.google.gson.annotations.SerializedName;

public class MapLocation {
    private float latitude;
    private float longitude;
    private String iconUrl;
    private String name;
    private String place_id;
    @SerializedName("vicinity")
    private String address;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getAddress() {
        return address;
    }
}
