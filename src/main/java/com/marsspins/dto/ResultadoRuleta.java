package com.marsspins.dto;

public record ResultadoRuleta(
        int indice,
        String casilla,
        String color,
        boolean gano,
        double apuesta,
        double ganancia,
        double saldo
) {}
