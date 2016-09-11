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
public class BlockNameByStoreNameAsync extends AsyncTask<String, Void, String> {

    private String blockName;
    private String entityId;
    private OnBlockNameReceivedListener listener;

    public BlockNameByStoreNameAsync(String blockName, OnBlockNameReceivedListener listener) {
        this.blockName = blockName;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        FiwareConnection fiwareConnection = new FiwareConnection();
        int maybeNextAdj = 0;
        try
        {
            entityId = fiwareConnection.getBlockIDByName(blockName);
            if(!entityId.equals("")){
                Boolean hasEmptyParkingSpot = fiwareConnection.lookForEmptyParkingSpace(entityId);
                while(!hasEmptyParkingSpot){
                    entityId = fiwareConnection.searchAdjacentBlockID(entityId,maybeNextAdj);
                    hasEmptyParkingSpot = fiwareConnection.lookForEmptyParkingSpace(entityId);
                    maybeNextAdj++;
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
        return blockName;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null){
            listener.onBlockNameReceivedListener(result);
        }
    }
}
