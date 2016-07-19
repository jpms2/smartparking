package br.cin.ufpe.inesescin.smartparking.service;

import android.location.Location;
import android.os.AsyncTask;

/**
 * Created by jal3 on 01/07/2016.
 */
public class LocationService extends AsyncTask<String, Void, Void>{
    private LocationListener locationListener;

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    protected Void doInBackground(String... params) {

        while(true){
            Location location = new Location("mockProvider");
            location.setLatitude(-8.086022);
            location.setLongitude(-34.891873);

            locationListener.onLocationChanged(location);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
