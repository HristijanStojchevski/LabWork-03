package com.example.mytestapplication.services;

import com.example.mytestapplication.model.MapBanklist;
import com.example.mytestapplication.model.MapLocation;
import com.google.android.gms.maps.model.LatLng;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface MapPlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    Call<MapBanklist> getLocations(@Query("location") LatLng latlong, @Query("key") String apiKey, @Query("radius") int radius, @Query("types") String placeType);
}
