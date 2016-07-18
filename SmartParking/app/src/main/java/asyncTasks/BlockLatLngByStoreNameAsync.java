package asyncTasks;

import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import br.cin.ufpe.inesescin.smartparking.util.Constants;
import connection.FiwareConnection;

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
        String storeName = params[0];
        String entityId = fiwareConnection.searchBlockByStore(storeName);
        try
        {
            String adjBlockID = fiwareConnection.getEntityAttributeValue("blocosAdjacentesID",entityId, Constants.FIWARE_ADDRESS,"value");
            if(fiwareConnection.lookForEmptyParkingSpace(entityId)){
                latLngArray =  fiwareConnection.getLatLng(fiwareConnection.getEntityAttributeValue("latLng", entityId, Constants.FIWARE_ADDRESS, "value"));
                latLng = new LatLng(latLngArray[0], latLngArray[1]);
            }else{
                if(fiwareConnection.lookForEmptyParkingSpace(adjBlockID)){
                    latLngArray =  fiwareConnection.getLatLng(fiwareConnection.getEntityAttributeValue("latLng", adjBlockID, Constants.FIWARE_ADDRESS, "value"));
                    latLng = new LatLng(latLngArray[0], latLngArray[1]);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
