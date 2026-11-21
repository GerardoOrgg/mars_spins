package com.marsspins.consola;

import com.marsspins.modelo.Pila;
import com.marsspins.modelo.TAD_CuentaUsuario;
import com.marsspins.servicio.RuletaService;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static com.marsspins.servicio.BlackjackService.calcularPuntajeMano;
import static com.marsspins.servicio.BlackjackService.crearMazoMezclado;

public class MarsSpinsConsola {
    public static String leer(Scanner sc) {
        if (sc.hasNextLine()) {
            return sc.nextLine().trim();
        }
        return "";
    }

    static final RuletaService ruletaService = new RuletaService();

    static void jugarBlackjack(TAD_CuentaUsuario cuenta, Scanner sc) {
        System.out.println("\n+-----------------------------------------------------+");
        System.out.println("|              MARS SPINS - BLACKJACK VIP             |");
        System.out.println("|  [Demostracion de Estructura: Pila Dinamica LIFO]   |");
        System.out.println("+-----------------------------------------------------+");

        Pila mazo = crearMazoMezclado();
        System.out.println("-> Mazo creado: 52 cartas apiladas con push().");
        System.out.println("-> Cima actual de la pila (peek()): " + mazo.peek());
        System.out.println("-> Cartas en pila: " + mazo.getTamanio());

        System.out.printf("%nSaldo disponible: $%.2f ficticios%n", cuenta.obtenerSaldo());
        System.out.print("Ingresa el monto a apostar: $");
        double apuesta = 0;
        try {
            apuesta = Double.parseDouble(leer(sc));
        } catch (Exception e) {
            System.out.println("Monto invalido.");
            return;
        }

        if (!cuenta.realizarApuesta(apuesta)) {
            System.out.println("[Error] Fondos insuficientes. Tu saldo actual es: $" + cuenta.obtenerSaldo());
            return;
        }
        System.out.println("[TAD] Apuesta aceptada. Saldo restante: $" + cuenta.obtenerSaldo());

        ArrayList<String> manoJugador = new ArrayList<>();
        ArrayList<String> manoDealer = new ArrayList<>();

        System.out.println("\n--- Repartiendo cartas iniciales usando pop() de la cima ---");
        manoJugador.add(mazo.pop());
        manoDealer.add(mazo.pop());
        manoJugador.add(mazo.pop());
        manoDealer.add(mazo.pop());

        System.out.println("-> Cartas restantes en pila tras repartir: " + mazo.getTamanio());
        System.out.println("-> Tu mano       : " + manoJugador + " [Puntos: " + calcularPuntajeMano(manoJugador) + "]");
        System.out.println("-> Mano del Dealer: [" + manoDealer.get(0) + ", <Carta Oculta>]");

        boolean turnoActivo = true;
        while (turnoActivo && calcularPuntajeMano(manoJugador) < 21) {
            System.out.println("\n¿Que accion deseas tomar?");
            System.out.println("  1. Pedir carta (pop() de la Pila)");
            System.out.println("  2. Plantarse");
            System.out.print("Selecciona (1 o 2): ");
            String op = leer(sc);

            if (op.equals("1")) {
                String cartaNueva = mazo.pop();
                manoJugador.add(cartaNueva);
                System.out.println("\n[POP] Carta extraida de la cima: " + cartaNueva);
                System.out.println("-> Tu mano actual: " + manoJugador + " [Puntos: " + calcularPuntajeMano(manoJugador) + "]");
                System.out.println("-> Cartas restantes en la pila: " + mazo.getTamanio());
            } else {
                turnoActivo = false;
            }
        }

        int ptosJugador = calcularPuntajeMano(manoJugador);

        System.out.println("\n--- Turno del Crupier / Dealer ---");
        System.out.println("-> Mano completa del Dealer: " + manoDealer);
        while (calcularPuntajeMano(manoDealer) < 17 && ptosJugador <= 21) {
            String cartaDealer = mazo.pop();
            manoDealer.add(cartaDealer);
            System.out.println("[Dealer POP] Extrae de la cima: " + cartaDealer);
        }
        int ptosDealer = calcularPuntajeMano(manoDealer);

        System.out.println("\n==========================================");
        System.out.println("           RESULTADO DE LA RONDA          ");
        System.out.println("==========================================");
        System.out.println("Puntaje Jugador : " + ptosJugador);
        System.out.println("Puntaje Dealer  : " + ptosDealer);

        if (ptosJugador > 21) {
            System.out.println("Te pasaste de 21 puntos. Pierdes la apuesta.");
        } else if (ptosJugador == 21 && manoJugador.size() == 2) {
            double premio = apuesta * 2.5;
            System.out.printf("¡BLACKJACK NATURAL! Ganaste $%.2f ficticios (pago 2.5x).%n", premio);
            cuenta.registrarResultado(premio);
        } else if (ptosDealer > 21) {
            double premio = apuesta * 2.0;
            System.out.printf("¡El Dealer se paso de 21! Ganaste $%.2f ficticios.%n", premio);
            cuenta.registrarResultado(premio);
        } else if (ptosJugador > ptosDealer) {
            double premio = apuesta * 2.0;
            System.out.printf("¡Tu mano supera a la del Dealer! Ganaste $%.2f ficticios.%n", premio);
            cuenta.registrarResultado(premio);
        } else if (ptosJugador == ptosDealer) {
            System.out.println("Empate. Se devuelve tu apuesta integra.");
            cuenta.registrarResultado(apuesta);
        } else {
            System.out.println("El Dealer gana la mano.");
        }

        cuenta.mostrarEstado();
    }

    static void jugarRuleta(TAD_CuentaUsuario cuenta, Scanner sc) {
        System.out.println("\n+-----------------------------------------------------+");
        System.out.println("|               MARS SPINS - RULETA                   |");
        System.out.println("|  [Demostracion de Estructura: Arreglo Fijo O(1)]   |");
        System.out.println("+-----------------------------------------------------+");

        System.out.println("Arreglo ruleta[37] con casillas fijas:");
        System.out.println("  Indice [0] : " + ruletaService.getCasilla(0));
        System.out.println("  Indice [1] : " + ruletaService.getCasilla(1));
        System.out.println("  ...");
        System.out.println("  Indice [36]: " + ruletaService.getCasilla(36));

        System.out.printf("%nSaldo disponible: $%.2f ficticios%n", cuenta.obtenerSaldo());
        System.out.print("¿A que color deseas apostar? (rojo / negro): ");
        String colorApuesta = leer(sc).toLowerCase();

        if (!colorApuesta.equals("rojo") && !colorApuesta.equals("negro")) {
            System.out.println("Color no valido. Debe ser 'rojo' o 'negro'.");
            return;
        }

        System.out.print("Ingresa el monto a apostar: $");
        double monto = 0;
        try {
            monto = Double.parseDouble(leer(sc));
        } catch (Exception e) {
            System.out.println("Monto invalido.");
            return;
        }

        if (!cuenta.realizarApuesta(monto)) {
            System.out.println("[Error] Saldo insuficiente.");
            return;
        }

        Random rnd = new Random();
        int indice_ganador = rnd.nextInt(37);
        String casilla_ganadora = ruletaService.getCasilla(indice_ganador);

        System.out.println("\nGirando la ruleta...");
        System.out.println("-> Indice aleatorio generado: " + indice_ganador);
        System.out.println("-> Casilla ganadora obtenida ruleta[" + indice_ganador + "]: " + casilla_ganadora);

        boolean gano = RuletaService.colorDe(casilla_ganadora).equals(colorApuesta);
        if (gano) {
            double ganancia = monto * 2.0;
            System.out.printf("¡Acertaste! Se te abonan $%.2f ficticios.%n", ganancia);
            cuenta.registrarResultado(ganancia);
        } else {
            System.out.println("No acertaste el color en esta tirada.");
        }

        cuenta.mostrarEstado();
    }

    static void demostracionPasoAPasoPila() {
        System.out.println("\n=======================================================");
        System.out.println("     DEMOSTRACION DE ESTRUCTURA DE DATOS: PILA (LIFO)  ");
        System.out.println("             (Para el Video de Entrega)                ");
        System.out.println("=======================================================");

        System.out.println("\n[1. EXPLICACION TEORICA Y JUSTIFICACION]");
        System.out.println("  * Que datos almacena: Las cartas de la baraja para Blackjack.");
        System.out.println("  * Principio de operacion: LIFO (Last In, First Out).");
        System.out.println("  * Justificacion: Una pila replica con exactitud el comportamiento");
        System.out.println("    fisico de un mazo de cartas: al mezclar se apilan (push),");
        System.out.println("    y al repartir, la carta entregada es siempre la que esta");
        System.out.println("    en la cima del mazo (pop), asegurando que ninguna carta");
        System.out.println("    se repita hasta volver a barajar.");

        System.out.println("\n[2. CREACION DE LA PILA]");
        Pila demoPila = new Pila();
        System.out.println("  -> Pila creada. ¿Esta vacia? " + demoPila.estaVacia());

        System.out.println("\n[3. OPERACION PUSH (Apilar cartas)]");
        System.out.println("  -> push(\"10 de Diamantes\")");
        demoPila.push("10 de Diamantes");
        System.out.println("  -> push(\"Reina de Corazones\")");
        demoPila.push("Reina de Corazones");
        System.out.println("  -> push(\"As de Picas\")");
        demoPila.push("As de Picas");

        System.out.println("\n  Contenido actual de la pila tras 3 push():");
        demoPila.mostrar();

        System.out.println("\n[4. OPERACION PEEK (Consultar la cima sin extraer)]");
        System.out.println("  -> peek(): " + demoPila.peek());
        System.out.println("  -> Cantidad de elementos (tamanio): " + demoPila.getTamanio());

        System.out.println("\n[5. OPERACION POP (Desapilar / Repartir carta)]");
        String cartaExtraida1 = demoPila.pop();
        System.out.println("  -> Se ejecuta pop(). Carta extraida de la cima: " + cartaExtraida1);
        System.out.println("     (Observe que salio 'As de Picas', la ultima en entrar)");

        System.out.println("\n  Contenido de la pila despues del primer pop():");
        demoPila.mostrar();

        String cartaExtraida2 = demoPila.pop();
        System.out.println("\n  -> Segundo pop(). Carta extraida: " + cartaExtraida2);
        System.out.println("  -> Nueva cima (peek()): " + demoPila.peek());
        System.out.println("  -> Cantidad restante: " + demoPila.getTamanio());

        System.out.println("\n=======================================================");
        System.out.println("     FIN DE LA DEMOSTRACION DE LA PILA                 ");
        System.out.println("=======================================================");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("+-----------------------------------------------------+");
        System.out.println("|           PROYECTO INTEGRADOR: MARS SPINS           |");
        System.out.println("|         Simulador de Casino con Saldo Ficticio      |");
        System.out.println("+-----------------------------------------------------+");

        System.out.print("Ingresa tu nombre de usuario para iniciar: ");
        String nombre = leer(sc);
        if (nombre.isEmpty()) {
            nombre = "Carlos Apreza";
        }

        TAD_CuentaUsuario cuenta = new TAD_CuentaUsuario(1, nombre, 1000.0);
        System.out.println("\n¡Bienvenido al sistema, " + nombre + "!");
        cuenta.mostrarEstado();

        boolean salir = false;
        while (!salir) {
            System.out.println("\n+-----------------------------------------------------+");
            System.out.println("|                     LOBBY PRINCIPAL                 |");
            System.out.println("+-----------------------------------------------------+");
            System.out.println("  1. Jugar BlackJack (Estructura: Pila LIFO)");
            System.out.println("  2. Jugar Ruleta (Estructura: Arreglo O(1))");
            System.out.println("  3. Demostracion paso a paso de la Pila (Para Video)");
            System.out.println("  4. Consultar Saldo / TAD_CuentaUsuario");
            System.out.println("  0. Salir");
            System.out.print("Selecciona una opcion: ");

            String opcion = leer(sc);
            if (opcion.isEmpty()) {
                break;
            }

            switch (opcion) {
                case "1":
                    jugarBlackjack(cuenta, sc);
                    break;
                case "2":
                    jugarRuleta(cuenta, sc);
                    break;
                case "3":
                    demostracionPasoAPasoPila();
                    break;
                case "4":
                    cuenta.mostrarEstado();
                    break;
                case "0":
                    salir = true;
                    System.out.println("\nGracias por jugar en Mars Spins. ¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opcion no valida. Intenta nuevamente.");
                    break;
            }
        }

        sc.close();
    }
}
