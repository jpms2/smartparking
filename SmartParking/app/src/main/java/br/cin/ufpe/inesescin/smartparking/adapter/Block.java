package br.cin.ufpe.inesescin.smartparking.adapter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by João Pedro on 27/06/2016.
 */
public class Block {

    private ArrayList lojas;
    private ArrayList vagas;
    private ArrayList blocosAdjacentesID;
    private LatLng latLng;

    public Block(ArrayList lojas, ArrayList vagas, ArrayList blocosAdjacentes, LatLng latLng){
        super();
        this.lojas = lojas;
        this.latLng = latLng;
        this.vagas = vagas;
        this.blocosAdjacentesID = blocosAdjacentes;
    }
    public ArrayList getVagas() {
        return vagas;
    }

    public ArrayList getLojas() {
        return lojas;
    }

    public ArrayList getBlocosAdjacentes() {
        return blocosAdjacentesID;
    }

    public LatLng getLatLong() {
        return latLng;
    }

}

