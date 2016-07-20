package br.cin.ufpe.inesescin.smartparking.service;

import android.location.Location;
import android.os.AsyncTask;

import br.cin.ufpe.inesescin.smartparking.MapsActivity;

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
        location.setLatitude(-8.086022);
        location.setLongitude(-34.891873);
            //  -8.085936, -34.891969

        return location;
    }

    @Override
    protected void onPostExecute(Location location) {
        locationListener.onLocationReceived(location);

    }
}
