package br.cin.ufpe.inesescin.smartparking.service;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jal3 on 18/07/2016.
 */
public class DirectionServiceImp implements IDirectionService {

    private DirectionListener directionListener;

    @Override
    public void getDirections(LatLng from, LatLng to) {
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(-8.084571, -34.895239));
        latLngs.add(new LatLng(-8.084621, -34.894788));
        latLngs.add(new LatLng(-8.084661, -34.894512));
        latLngs.add(new LatLng(-8.084884, -34.894539));

        directionListener.onDirectionReceived(latLngs);
    }

    @Override
    public void setDirectionListener(DirectionListener directionListener) {
        this.directionListener = directionListener;
    }


}
