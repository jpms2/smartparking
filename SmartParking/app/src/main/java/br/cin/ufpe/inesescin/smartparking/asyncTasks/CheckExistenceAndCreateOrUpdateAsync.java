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
public class CheckExistenceAndCreateOrUpdateAsync extends AsyncTask<String, Void, Void> {

    String username;
    String storeName;
    FiwareConnection fiwareConnection = new FiwareConnection();

    public CheckExistenceAndCreateOrUpdateAsync(String username, String storeName){
        this.username = username;
        this.storeName = storeName;
    }

    @Override
    protected Void doInBackground(String... params) {
        boolean check;
        try {
            String blockID = fiwareConnection.getBlockIDByName(storeName);
            if(!blockID.equals("")){
                check = fiwareConnection.checkForUserPreferences(username, Constants.LOCALHOST_ADDRESS);
                if(check){
                    fiwareConnection.updateUserPreferences(username,blockID,Constants.LOCALHOST_ADDRESS);
                }else{
                    fiwareConnection.addUserPreferences(username,blockID,Constants.LOCALHOST_ADDRESS);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
