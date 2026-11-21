package com.marsspins.dto;

import com.marsspins.modelo.TAD_CuentaUsuario;

public record CuentaDTO(int id, String nombre, double saldo) {
    public static CuentaDTO de(TAD_CuentaUsuario cuenta) {
        return new CuentaDTO(cuenta.getIdUsuario(), cuenta.getNombre(), cuenta.obtenerSaldo());
    }
}
