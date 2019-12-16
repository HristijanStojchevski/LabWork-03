package com.example.mytestapplication.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeoTrackingService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_CURR_LOCATION = "com.example.mytestapplication.services.action.GET_CURR_LOCATION";

    Logger logger = Logger.getLogger("GeoTrackingService");
    public GeoTrackingService() {
        super("GeoTrackingService");
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logger.info("This is service logic");
        if (intent != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            Location location = intent.getParcelableExtra("locationData");
            List<Address> addresses = null;
            try{
                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            }
            catch(Exception e){
                logger.severe("No addresses");

            }
            if(addresses == null || addresses.size()==0){ logger.severe("No addresses found");}
            else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();
                for(int i=0; i<= address.getMaxAddressLineIndex();i++){
                    addressFragments.add(address.getAddressLine(i));
                }

            }
        }

    }


}
