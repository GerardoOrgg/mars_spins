package com.marsspins.modelo;

import java.io.Serializable;

public class TAD_CuentaUsuario implements Serializable {
    private int id_usuario;
    private String nombre;
    private double saldo_actual;

    public TAD_CuentaUsuario(int id_usuario, String nombre, double saldo_inicial) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.saldo_actual = saldo_inicial;
    }

    public void iniciarCuenta(int id, String nom, double saldo) {
        this.id_usuario = id;
        this.nombre = nom;
        this.saldo_actual = saldo;
    }

    public double obtenerSaldo() {
        return this.saldo_actual;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getIdUsuario() {
        return this.id_usuario;
    }

    public boolean realizarApuesta(double cantidad) {
        if (cantidad > 0 && cantidad <= this.saldo_actual) {
            this.saldo_actual -= cantidad;
            return true;
        }
        return false;
    }

    public void registrarResultado(double ganancia_obtenida) {
        this.saldo_actual += ganancia_obtenida;
    }

    public void recargar(double cantidad) {
        if (cantidad > 0) {
            this.saldo_actual += cantidad;
        }
    }

    public void mostrarEstado() {
        System.out.println("+-----------------------------------------+");
        System.out.println("|       TAD_CuentaUsuario (Encapsulado)   |");
        System.out.println("+-----------------------------------------+");
        System.out.println("  ID Usuario     : " + this.id_usuario);
        System.out.println("  Titular        : " + this.nombre);
        System.out.printf ("  Saldo Ficticio : $%.2f%n", this.saldo_actual);
        System.out.println("+-----------------------------------------+");
    }
}
