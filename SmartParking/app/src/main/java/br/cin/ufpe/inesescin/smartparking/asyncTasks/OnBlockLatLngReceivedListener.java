package br.cin.ufpe.inesescin.smartparking.asyncTasks;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by João Pedro on 20/07/2016.
 */
public interface OnBlockLatLngReceivedListener {
    public void onBlockLatLngReceived(LatLng latLng);
}
