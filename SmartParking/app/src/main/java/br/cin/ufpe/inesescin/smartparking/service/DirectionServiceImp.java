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
        latLngs.add(new LatLng(-8.084925, -34.894689));
        latLngs.add(new LatLng(-8.085482, -34.894750));
        latLngs.add(new LatLng(-8.085397, -34.894499));
        latLngs.add(new LatLng(-8.085386, -34.894248));

        directionListener.onDirectionReceived(latLngs);
    }

    @Override
    public void setDirectionListener(DirectionListener directionListener) {
        this.directionListener = directionListener;
    }


}
