package com.marsspins.dto;

import com.marsspins.modelo.PartidaBlackjack;
import com.marsspins.servicio.BlackjackService;

import java.util.ArrayList;
import java.util.List;

public record EstadoBlackjack(
        boolean activa,
        List<String> manoJugador,
        List<String> manoDealer,
        int puntosJugador,
        int puntosDealer,
        double apuesta,
        int cartasEnPila,
        String resultado,
        String mensaje,
        double pago,
        double saldo
) {
    public static EstadoBlackjack de(PartidaBlackjack p, double saldo) {
        if (p == null) {
            return new EstadoBlackjack(false, List.of(), List.of(), 0, 0, 0, 52,
                    null, null, 0, saldo);
        }

        List<String> dealerVisible = new ArrayList<>(p.getManoDealer());
        int puntosDealer;
        if (p.isTerminada()) {
            puntosDealer = BlackjackService.calcularPuntajeMano(dealerVisible);
        } else {
            for (int i = 1; i < dealerVisible.size(); i++) {
                dealerVisible.set(i, null);
            }
            puntosDealer = BlackjackService.calcularPuntajeMano(List.of(p.getManoDealer().get(0)));
        }

        return new EstadoBlackjack(
                !p.isTerminada(),
                List.copyOf(p.getManoJugador()),
                dealerVisible,
                BlackjackService.calcularPuntajeMano(p.getManoJugador()),
                puntosDealer,
                p.getApuesta(),
                p.getMazo().getTamanio(),
                p.getResultado(),
                p.getMensaje(),
                p.getPago(),
                saldo
        );
    }
}
