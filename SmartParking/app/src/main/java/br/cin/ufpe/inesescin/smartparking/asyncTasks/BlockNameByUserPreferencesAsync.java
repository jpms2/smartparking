package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

import br.cin.ufpe.inesescin.smartparking.connection.FiwareConnection;
import br.cin.ufpe.inesescin.smartparking.util.Constants;

/**
 * Created by João Pedro on 03/08/2016.
 */
public class BlockNameByUserPreferencesAsync extends AsyncTask<String, Void, String> {

    String username;
    OnBlockNameReceivedListener listener;
    FiwareConnection fiwareConnection = new FiwareConnection();

    public BlockNameByUserPreferencesAsync(String username, OnBlockNameReceivedListener listener) {
        this.username = username;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String blockID = "";
        String res = "";
        try {
            blockID = fiwareConnection.getBlockIDByPreferences(username, Constants.LOCALHOST_ADDRESS);
            res = fiwareConnection.getBlockNameByID(blockID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(result != null){
            listener.onBlockNameReceivedListener(result);
        }
    }
}
