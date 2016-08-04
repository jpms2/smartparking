package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.connection.FiwareConnection;
import br.cin.ufpe.inesescin.smartparking.util.Constants;

/**
 * Created by João Pedro on 03/08/2016.
 */
public class LatLngByUserPreferencesAsync extends AsyncTask<String, Void, LatLng> {

    String username;
    OnBlockLatLngReceivedListener listener;
    LatLng latLng;
    FiwareConnection fiwareConnection = new FiwareConnection();

    public LatLngByUserPreferencesAsync(String username, OnBlockLatLngReceivedListener listener) {
        this.username = username;
        this.listener = listener;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        String blockID = "";
        Double[] res;
        try {
            blockID = fiwareConnection.getBlockIDByPreferences(username, Constants.LOCALHOST_ADDRESS);
            res = fiwareConnection.getLatLng(blockID);
            latLng = new LatLng(res[0], res[1]);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latLng;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        super.onPostExecute(result);
        if(result != null){
            listener.onBlockLatLngReceived(result);
        }
    }

}
