package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.connection.FiwareConnection;

/**
 * Created by João Pedro on 18/07/2016.
 */
public class BlockLatLngByStoreNameAsync extends AsyncTask<String, Void, LatLng> {

    private String storeName;
    LatLng latLng;
    Double[] latLngArray = new Double[2];

    public BlockLatLngByStoreNameAsync(String storeName) {
        this.storeName = storeName;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        FiwareConnection fiwareConnection = new FiwareConnection();
        String entityId = "";
        int maybeNextAdj = 0;
        try
        {
            entityId = fiwareConnection.getBlockIDByStore(storeName);
            if(!entityId.equals("")){
                Boolean hasEmptyParkingSpot = fiwareConnection.lookForEmptyParkingSpace(entityId);
                while(!hasEmptyParkingSpot){
                    String adjBlockID = fiwareConnection.searchAdjacentBlockID(entityId,maybeNextAdj);
                    hasEmptyParkingSpot = fiwareConnection.lookForEmptyParkingSpace(adjBlockID);
                    maybeNextAdj++;
                }
                latLngArray = fiwareConnection.getLatLng(entityId);
                latLng = new LatLng(latLngArray[0],latLngArray[1]);
            }else{
                //Procurou por loja que não existe!
                latLng = new LatLng(0,0);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }catch (JSONException j){
            j.printStackTrace();
        }
        return latLng;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        super.onPostExecute(result);
        if(result != null && !result.equals(""))
        {

        }
    }
}
