package com.example.mytestapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.mytestapplication.model.MapBanklist;
import com.example.mytestapplication.model.MapLocation;
import com.example.mytestapplication.services.GeoTrackingService;
import com.example.mytestapplication.services.MapPlacesApiService;
import com.example.mytestapplication.services.UpdateNearBanksService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.*;
import java.util.logging.Logger;

public class GooglePlacesActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static GoogleMap mMap;
    private LocationManager locationManager;
    private Marker markerOnTheMove;
    private PlacesClient placesClient;
    private LocationCallback locationCallback;
    private LatLng currLocation;
    private MapPlacesApiService mapPlacesApiService;
    Logger logger = Logger.getLogger("GooglePlacesActivity");
    private Timer repeatTask;
    private int repeatInterval = 60000; //60000ms => 1min
    //private List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.LAT_LNG);
    private Button btnFindBanks;

    @Override
    protected void onStart() {
        super.onStart();
        listenLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(repeatTask!=null){
            repeatTask.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        btnFindBanks = findViewById(R.id.btn_findbanks);

        initPlaces();
    }

    private void initPlaces() {
        Places.initialize(this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /* Set up the location btn on the MAP
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true); */
        // Add a marker in Sydney and move the camera
        LatLng skopje = new LatLng(42.015, 21.419);
        markerOnTheMove = mMap.addMarker(new MarkerOptions().position(skopje).title("Marker in Skopje"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(skopje));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(11f));

        btnFindBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearchBankService();
            }
        });

    }

    private void startSearchBankService() {
        Intent startSearchService = new Intent(this, UpdateNearBanksService.class);
        currLocation = markerOnTheMove.getPosition();
        startSearchService.putExtra("locationLatLong", currLocation);
        getBanks();
        /* run service on btn click... Plus the idea of getting new user location always and only updating the banks after 1min
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(currLocation));
        startService(startSearchService); */
    }

    private void getBanks() {
        String latlongi = markerOnTheMove.getPosition().latitude+","+markerOnTheMove.getPosition().longitude;
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
                if(searchData.getResult()!=null) {
                    for (MapLocation place : searchData.getResult()) {

                        double lat = place.getGeometry().getLocation().getLatitude();
                        double lng = place.getGeometry().getLocation().getLongitude();
                        String placeName = place.getName();
                        String vicinity = place.getAddress();
                        String iconUrl = place.getIconUrl();
                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName + " : " + vicinity);
                        // Zoshto prvo dodava marker pa setira stil na markerot..
                        Marker m = mMap.addMarker(markerOptions);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        //logger.info(content);
                    }
                }
            }

            @Override
            public void onFailure(Call<MapBanklist> call, Throwable t) {
                logger.info("Call Failed: " + t.getMessage());
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOnTheMove.setPosition(latLng);
        markerOnTheMove.setTitle("Personal pin");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOnTheMove.getPosition(), 12f));
        getBanks();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void listenLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, this);

    }
    private boolean checkPermisions(){
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return false;
        }
        return true;
    }

    public void setlocManually(View view) {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
            return;
        }
        Intent intent = new Intent(this, GeoTrackingService.class);
        intent.putExtra("locationData", locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        startService(intent);
    }
}
