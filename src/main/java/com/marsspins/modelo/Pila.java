package com.marsspins.modelo;

import java.io.Serializable;

public class Pila implements Serializable {
    Nodo cima;
    int tamanio;

    public Pila() {
        this.cima = null;
        this.tamanio = 0;
    }

    public boolean estaVacia() {
        return this.cima == null;
    }

    public void push(String carta) {
        Nodo nuevo = new Nodo(carta);
        nuevo.siguiente = this.cima;
        this.cima = nuevo;
        this.tamanio++;
    }

    public String pop() {
        if (estaVacia()) {
            return null;
        }
        String cartaExtraida = this.cima.carta;
        this.cima = this.cima.siguiente;
        this.tamanio--;
        return cartaExtraida;
    }

    public String peek() {
        if (estaVacia()) {
            return null;
        }
        return this.cima.carta;
    }

    public void mostrar() {
        if (estaVacia()) {
            System.out.println("  [La pila esta vacia]");
            return;
        }
        Nodo actual = this.cima;
        int posicion = 1;
        while (actual != null) {
            String etiqueta = (posicion == 1) ? " (CIMA)" : "";
            System.out.println("  [" + posicion + "] " + actual.carta + etiqueta);
            actual = actual.siguiente;
            posicion++;
        }
    }

    public int getTamanio() {
        return this.tamanio;
    }
}
