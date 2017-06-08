package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.connection.FiwareConnection;
import br.cin.ufpe.inesescin.smartparking.util.Constants;

/**
 * Created by João Pedro on 18/07/2016.
 */
//THIS CLASS WAS CURRENTLY CHANGED TO FIT NEW PURPOSES, TO GO BACK CHANGE RETURN TO BLOCKNAME
public class BlockNameByStoreNameAsync extends AsyncTask<String, Void, String> {

    private String storeName;
    private String blockName;
    private String entityId;
    private OnBlockNameReceivedListener listener;

    public BlockNameByStoreNameAsync(String storeName, OnBlockNameReceivedListener listener) {
        this.storeName = storeName;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        FiwareConnection fiwareConnection = new FiwareConnection();
        int maybeNextAdj = 0;
        try
        {
            entityId = fiwareConnection.getBlockIDByStore(storeName);
            if(!entityId.equals("")){
                Boolean hasEmptyParkingSpot = fiwareConnection.lookForEmptyParkingSpace(entityId);
                if(!hasEmptyParkingSpot){
                    entityId = fiwareConnection.searchAdjacentBlockID(entityId,maybeNextAdj);
                    blockName = fiwareConnection.getBlockNameByID(entityId);
                }else{
                    blockName = fiwareConnection.getBlockNameByID(entityId);
                }
            }else{
                blockName = "nulo";
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }catch (JSONException j){
            j.printStackTrace();
        }
        return storeName;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null){
            listener.onBlockNameReceivedListener(result);
        }
    }
}
