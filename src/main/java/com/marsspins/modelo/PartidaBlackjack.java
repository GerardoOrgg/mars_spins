package com.marsspins.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PartidaBlackjack implements Serializable {
    private final Pila mazo;
    private final List<String> manoJugador = new ArrayList<>();
    private final List<String> manoDealer = new ArrayList<>();
    private double apuesta;
    private boolean terminada = false;
    private String resultado;
    private String mensaje;
    private double pago;

    public PartidaBlackjack(Pila mazo, double apuesta) {
        this.mazo = mazo;
        this.apuesta = apuesta;
    }

    public Pila getMazo() { return mazo; }
    public List<String> getManoJugador() { return manoJugador; }
    public List<String> getManoDealer() { return manoDealer; }

    public double getApuesta() { return apuesta; }
    public void setApuesta(double apuesta) { this.apuesta = apuesta; }

    public boolean isTerminada() { return terminada; }

    public String getResultado() { return resultado; }
    public String getMensaje() { return mensaje; }
    public double getPago() { return pago; }

    public void terminar(String resultado, String mensaje, double pago) {
        this.terminada = true;
        this.resultado = resultado;
        this.mensaje = mensaje;
        this.pago = pago;
    }
}
