package com.example.mytestapplication.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MapBanklist {
    public List<MapLocation> result;

    public List<MapLocation> getResult() {
        return result;
    }

    public void setResult(List<MapLocation> result) {
        this.result = result;
    }
}
