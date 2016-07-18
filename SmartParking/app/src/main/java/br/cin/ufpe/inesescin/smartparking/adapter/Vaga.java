package br.cin.ufpe.inesescin.smartparking.adapter;

/**
 * Created by João Pedro on 18/07/2016.
 */
public class Vaga {

    private int id;
    private boolean ocupado;

    public Vaga(int id, boolean ocupado){
        this.id = id;
        this.ocupado = ocupado;
    }

    public int getId() {
        return id;
    }

    public Boolean getOcupado() { return ocupado; }
}
