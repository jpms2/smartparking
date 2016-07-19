package br.cin.ufpe.inesescin.smartparking.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by jal3 on 18/07/2016.
 */
public interface DirectionListener {
    public void onDirectionReceived(List<LatLng> latLngs);
}
