package com.marsspins.modelo;

import java.io.Serializable;

public class Nodo implements Serializable {
    String carta;
    Nodo siguiente;

    public Nodo(String carta) {
        this.carta = carta;
        this.siguiente = null;
    }
}
