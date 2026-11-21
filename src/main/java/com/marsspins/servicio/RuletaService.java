package com.marsspins.servicio;

import com.marsspins.dto.ResultadoRuleta;
import com.marsspins.modelo.TAD_CuentaUsuario;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RuletaService {
    private final String[] ruleta = new String[37];
    private final Random rnd = new Random();

    public RuletaService() {
        inicializarRuleta();
    }

    private void inicializarRuleta() {
        int[] rojos = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        ruleta[0] = "0 - Verde";
        for (int i = 1; i <= 36; i++) {
            boolean esRojo = false;
            for (int r : rojos) {
                if (r == i) {
                    esRojo = true;
                    break;
                }
            }
            ruleta[i] = i + (esRojo ? " - Rojo" : " - Negro");
        }
    }

    public String getCasilla(int indice) {
        return ruleta[indice];
    }

    public String[] getCasillas() {
        return ruleta.clone();
    }

    public static String colorDe(String casilla) {
        return casilla.substring(casilla.indexOf('-') + 1).trim().toLowerCase();
    }

    public ResultadoRuleta girar(TAD_CuentaUsuario cuenta, String colorApuesta, Double monto) {
        String color = colorApuesta == null ? "" : colorApuesta.trim().toLowerCase();
        if (!color.equals("rojo") && !color.equals("negro")) {
            throw new MarsSpinsException("Color no valido. Debe ser 'rojo' o 'negro'.");
        }
        if (monto == null || monto <= 0) {
            throw new MarsSpinsException("Ingresa un monto mayor a 0");
        }

        if (!cuenta.realizarApuesta(monto)) {
            throw new MarsSpinsException("Saldo insuficiente");
        }

        int indiceGanador = rnd.nextInt(37);
        String casillaGanadora = ruleta[indiceGanador];

        boolean gano = colorDe(casillaGanadora).equals(color);
        double ganancia = 0;
        if (gano) {
            ganancia = monto * 2.0;
            cuenta.registrarResultado(ganancia);
        }

        return new ResultadoRuleta(indiceGanador, casillaGanadora, colorDe(casillaGanadora),
                gano, monto, ganancia, cuenta.obtenerSaldo());
    }
}
