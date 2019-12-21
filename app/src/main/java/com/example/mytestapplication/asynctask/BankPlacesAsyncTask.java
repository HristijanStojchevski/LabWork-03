package com.example.mytestapplication.asynctask;

import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;

public class BankPlacesAsyncTask extends AsyncTask<Object,String,String> {
    private String googlePlacesData;
    private GoogleMap mMap;
    private String url;

    @Override
    protected String doInBackground(Object... objects) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
