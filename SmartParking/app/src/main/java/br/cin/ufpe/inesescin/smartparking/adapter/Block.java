package br.cin.ufpe.inesescin.smartparking.adapter;

import java.util.ArrayList;

/**
 * Created by João Pedro on 27/06/2016.
 */
public class Block {

    private ArrayList lojas;
    private ArrayList vagas;
    private ArrayList<Block> blocosAdjacentes;

    public Block(ArrayList vagas, ArrayList<Block> blocosAdjacentes){
        super();
        this.vagas = vagas;
        this.blocosAdjacentes = blocosAdjacentes;
    }

    public Block(){
    }

    public ArrayList getVagas() {
        return vagas;
    }

    public void setVagas(ArrayList vagas) {
        this.vagas = vagas;
    }

    public ArrayList getLojas() {
        return lojas;
    }

    public void setLojas(ArrayList lojas) {
        this.lojas = lojas;
    }

    public ArrayList<Block> getBlocosAdjacentes() {
        return blocosAdjacentes;
    }

    public void setBlocosAdjacentes(ArrayList<Block> blocosAdjacentes) {
        this.blocosAdjacentes = blocosAdjacentes;
    }
}
