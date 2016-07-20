package br.cin.ufpe.inesescin.smartparking.service;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by jal3 on 01/07/2016.
 */
public class LocationService extends AsyncTask<String, Location, Void>{
    private LocationListener locationListener;

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    protected Void doInBackground(String... params) {

        Location location = new Location("mockProvider");
        location.setLatitude(-8.086022);
        location.setLongitude(-34.891873);
            //  -8.085936, -34.891969

            //usa o metodo OnProgressUpdate para atualizar o listener de forma correta
//            publishProgress(location);
        return null;
    }

    @Override
    protected void onProgressUpdate(Location... values) {
        super.onProgressUpdate(values);
        locationListener.onLocationChanged(values[0]);
    }
}
