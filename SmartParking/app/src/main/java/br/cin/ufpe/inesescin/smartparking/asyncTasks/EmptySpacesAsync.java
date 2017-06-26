package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.connection.FiwareConnection;
import br.cin.ufpe.inesescin.smartparking.util.Constants;

/**
 * Created by jpms2 on 26/06/2017.
 */
public class EmptySpacesAsync extends AsyncTask<String[], Void, String[]> {

    private OnEmptySpacesReceivedListener listener;

    public EmptySpacesAsync(OnEmptySpacesReceivedListener listener){
        this.listener = listener;
    }

    @Override
    protected String[] doInBackground(String[]... params) {
        FiwareConnection fiwareConnection = new FiwareConnection();
        String[] emptySpaces = new String[7];
        try {
            emptySpaces = fiwareConnection.getEntitiesByType(Constants.FIWARE_ADDRESS,"Block");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return emptySpaces;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        if(result != null){
            listener.OnEmptySpacesReceivedListener(result);
        }
    }
}
