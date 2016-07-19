package br.cin.ufpe.inesescin.smartparking.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by jal3 on 18/07/2016.
 */
public interface IDirectionService {
    public void getDirections(LatLng from, LatLng to);

    public void setDirectionListener(DirectionListener directionListener);
}
