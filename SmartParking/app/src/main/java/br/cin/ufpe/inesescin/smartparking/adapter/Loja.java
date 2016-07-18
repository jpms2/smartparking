package br.cin.ufpe.inesescin.smartparking.adapter;

/**
 * Created by João Pedro on 18/07/2016.
 */
public class Loja {

    private int id;
    private String nome;

    public Loja(int id, String nome){
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }
}
