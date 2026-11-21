package com.marsspins.servicio;

import com.marsspins.modelo.PartidaBlackjack;
import com.marsspins.modelo.Pila;
import com.marsspins.modelo.TAD_CuentaUsuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BlackjackService {
    public static Pila crearMazoMezclado() {
        String[] palos = {"Picas", "Corazones", "Diamantes", "Treboles"};
        String[] valores = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        ArrayList<String> barajaAux = new ArrayList<>();
        for (String p : palos) {
            for (String v : valores) {
                barajaAux.add(v + " de " + p);
            }
        }

        Collections.shuffle(barajaAux);

        Pila mazoPila = new Pila();
        for (String c : barajaAux) {
            mazoPila.push(c);
        }
        return mazoPila;
    }

    public static int obtenerValorNumerico(String carta) {
        String val = carta.split(" de ")[0].trim();
        if (val.equals("A")) {
            return 11;
        }
        if (val.equals("J") || val.equals("Q") || val.equals("K")) {
            return 10;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 10;
        }
    }

    public static int calcularPuntajeMano(List<String> mano) {
        int total = 0;
        int ases = 0;
        for (String c : mano) {
            total += obtenerValorNumerico(c);
            if (c.startsWith("A de ")) {
                ases++;
            }
        }

        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }

    public PartidaBlackjack iniciar(TAD_CuentaUsuario cuenta, Double apuesta) {
        if (apuesta == null || apuesta <= 0) {
            throw new MarsSpinsException("Elige un monto para apostar");
        }

        if (!cuenta.realizarApuesta(apuesta)) {
            throw new MarsSpinsException("Fondos insuficientes para esa apuesta");
        }

        PartidaBlackjack partida = new PartidaBlackjack(crearMazoMezclado(), apuesta);
        Pila mazo = partida.getMazo();
        partida.getManoJugador().add(mazo.pop());
        partida.getManoDealer().add(mazo.pop());
        partida.getManoJugador().add(mazo.pop());
        partida.getManoDealer().add(mazo.pop());

        if (calcularPuntajeMano(partida.getManoJugador()) == 21) {
            resolver(partida, cuenta);
        }
        return partida;
    }

    public void pedir(PartidaBlackjack partida, TAD_CuentaUsuario cuenta) {
        validarEnCurso(partida);
        partida.getManoJugador().add(partida.getMazo().pop());
        if (calcularPuntajeMano(partida.getManoJugador()) >= 21) {
            resolver(partida, cuenta);
        }
    }

    public void plantarse(PartidaBlackjack partida, TAD_CuentaUsuario cuenta) {
        validarEnCurso(partida);
        resolver(partida, cuenta);
    }

    public void doblar(PartidaBlackjack partida, TAD_CuentaUsuario cuenta) {
        validarEnCurso(partida);
        if (partida.getManoJugador().size() != 2) {
            throw new MarsSpinsException("Solo puedes doblar con tus 2 primeras cartas");
        }
        if (!cuenta.realizarApuesta(partida.getApuesta())) {
            throw new MarsSpinsException("No te alcanza el saldo para doblar");
        }
        partida.setApuesta(partida.getApuesta() * 2);
        partida.getManoJugador().add(partida.getMazo().pop());
        resolver(partida, cuenta);
    }

    private void validarEnCurso(PartidaBlackjack partida) {
        if (partida == null || partida.isTerminada()) {
            throw new MarsSpinsException("No hay una mano en curso. Presiona Repartir.");
        }
    }

    private void resolver(PartidaBlackjack partida, TAD_CuentaUsuario cuenta) {
        List<String> manoJugador = partida.getManoJugador();
        List<String> manoDealer = partida.getManoDealer();
        double apuesta = partida.getApuesta();

        int ptosJugador = calcularPuntajeMano(manoJugador);

        while (calcularPuntajeMano(manoDealer) < 17 && ptosJugador <= 21) {
            manoDealer.add(partida.getMazo().pop());
        }
        int ptosDealer = calcularPuntajeMano(manoDealer);

        if (ptosJugador > 21) {
            partida.terminar("TE_PASASTE", "Te pasaste de 21. Pierdes la apuesta.", 0);
        } else if (ptosJugador == 21 && manoJugador.size() == 2) {
            double premio = apuesta * 2.5;
            cuenta.registrarResultado(premio);
            partida.terminar("BLACKJACK", "¡BLACKJACK NATURAL! Pago 2.5x", premio);
        } else if (ptosDealer > 21) {
            double premio = apuesta * 2.0;
            cuenta.registrarResultado(premio);
            partida.terminar("GANASTE", "¡El dealer se paso de 21! Ganaste", premio);
        } else if (ptosJugador > ptosDealer) {
            double premio = apuesta * 2.0;
            cuenta.registrarResultado(premio);
            partida.terminar("GANASTE", "¡Tu mano supera a la del dealer! Ganaste", premio);
        } else if (ptosJugador == ptosDealer) {
            cuenta.registrarResultado(apuesta);
            partida.terminar("EMPATE", "Empate. Se devuelve tu apuesta.", apuesta);
        } else {
            partida.terminar("PERDISTE", "El dealer gana la mano.", 0);
        }
    }
}
