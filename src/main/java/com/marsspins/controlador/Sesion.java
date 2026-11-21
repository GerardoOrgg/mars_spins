package com.marsspins.controlador;

import com.marsspins.modelo.TAD_CuentaUsuario;
import com.marsspins.servicio.CuentaService;
import com.marsspins.servicio.NoAutenticadoException;
import jakarta.servlet.http.HttpSession;

final class Sesion {
    static final String USUARIO = "usuario";
    static final String PARTIDA_BLACKJACK = "partidaBlackjack";

    private Sesion() {}

    static TAD_CuentaUsuario cuentaActual(HttpSession sesion, CuentaService cuentaService) {
        TAD_CuentaUsuario cuenta = cuentaService.buscar((String) sesion.getAttribute(USUARIO));
        if (cuenta == null) {
            throw new NoAutenticadoException();
        }
        return cuenta;
    }
}
