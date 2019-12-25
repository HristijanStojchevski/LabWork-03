package com.example.mytestapplication.model;

import com.google.gson.annotations.SerializedName;

public class MapLocation {
    private Geometry geometry;
    @SerializedName("icon")
    private String iconUrl;
    private String name;
    private String place_id;
    @SerializedName("vicinity")
    private String address;

    public Geometry getGeometry() {
        return geometry;
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

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
