package com.example.mytestapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.mytestapplication.GooglePlacesActivity;
import com.example.mytestapplication.R;
import com.example.mytestapplication.model.MapBanklist;
import com.example.mytestapplication.model.MapLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.logging.Logger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class UpdateNearBanksService extends IntentService {
     private static final String ACTION_FOO = "com.example.mytestapplication.services.action.GET_NEAR_RESTAURANTS";
     private Timer repeatTask;
     private int repeatInterval = 60000; //60000ms => 1min
     //private List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.LAT_LNG);
     private MapPlacesApiService mapPlacesApiService;
     private Logger logger= Logger.getLogger("UpdateNearBanks");
    //private GooglePlacesActivity subMap = new GooglePlacesActivity();
     public UpdateNearBanksService() {
        super("UpdateNearBanksService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mapPlacesApiService = retrofit.create(MapPlacesApiService.class);
            LatLng locationLatLong = intent.getParcelableExtra("locationLatLong");
            getBanksUpdate(locationLatLong);
            // My idea for the task
             /* repeatTask = new Timer();
            repeatTask.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //Execute bank search update
                    //LatLng latLng = GooglePlacesActivity.markerOnTheMove.getPosition();
                    //getBanksUpdate(latLng);
                }
            },0,repeatInterval);
            */
        }
    }

    private void getBanksUpdate(final LatLng latLng) {
        float latitude = (float) latLng.latitude;
        float longitude= (float) latLng.longitude;
        String latlongi = latLng.latitude+","+latLng.longitude;
        Call<MapBanklist> call = mapPlacesApiService.getLocations(latlongi, getString(R.string.google_maps_key),5000,"bank");
        //Logger logger = new Logger();
        call.enqueue(new Callback<MapBanklist>() {
            @Override
            public void onResponse(Call<MapBanklist> call, Response<MapBanklist> response) {
                if(!response.isSuccessful()){
                    logger.info("Code: " + response.code());
                    return;
                }
                MapBanklist searchData = response.body();
                for(MapLocation location : searchData.result)
                {
                    double lat = location.getGeometry().getLocation().getLatitude();
                    double lng = location.getGeometry().getLocation().getLongitude();
                    String placeName = location.getName();
                    String vicinity = location.getAddress();
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(lat,lng);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    // It would require static mMap or just public
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    //Marker m = GooglePlacesActivity.mMap.addMarker(markerOptions);
                    //logger.info(content);
                }
            }

            @Override
            public void onFailure(Call<MapBanklist> call, Throwable t) {
                logger.info("Call Failed: " + t.getMessage());
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(repeatTask!=null){
            repeatTask.cancel();
        }
    }
}
