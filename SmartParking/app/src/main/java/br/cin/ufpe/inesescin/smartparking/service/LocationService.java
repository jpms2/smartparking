package br.cin.ufpe.inesescin.smartparking.service;

import android.location.Location;
import android.os.AsyncTask;

/**
 * Created by jal3 on 01/07/2016.
 */
public class LocationService extends AsyncTask<String, Location, Location>{
    private LocationListener locationListener;

    public LocationService(LocationListener locationListener) {
        setLocationListener(locationListener);
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    protected Location doInBackground(String... params) {

        Location location = new Location("mockProvider");
        location.setLatitude(-8.084571);
        location.setLongitude(-34.895239);
            //  -8.084571, -34.895239

        return location;
    }

    @Override
    protected void onPostExecute(Location location) {
        locationListener.onLocationReceived(location);

    }
}
