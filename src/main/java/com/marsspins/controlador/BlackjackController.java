package com.marsspins.controlador;

import com.marsspins.dto.EstadoBlackjack;
import com.marsspins.dto.Peticiones;
import com.marsspins.modelo.PartidaBlackjack;
import com.marsspins.modelo.TAD_CuentaUsuario;
import com.marsspins.servicio.BlackjackService;
import com.marsspins.servicio.CuentaService;
import com.marsspins.servicio.MarsSpinsException;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/blackjack")
public class BlackjackController {
    private final BlackjackService blackjackService;
    private final CuentaService cuentaService;

    public BlackjackController(BlackjackService blackjackService, CuentaService cuentaService) {
        this.blackjackService = blackjackService;
        this.cuentaService = cuentaService;
    }

    @GetMapping("/estado")
    public EstadoBlackjack estado(HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        return EstadoBlackjack.de(partida(sesion), cuenta.obtenerSaldo());
    }

    @PostMapping("/iniciar")
    public EstadoBlackjack iniciar(@RequestBody Peticiones.Apuesta datos, HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        PartidaBlackjack actual = partida(sesion);
        if (actual != null && !actual.isTerminada()) {
            throw new MarsSpinsException("Termina la mano actual antes de repartir otra");
        }
        PartidaBlackjack nueva = blackjackService.iniciar(cuenta, datos.monto());
        sesion.setAttribute(Sesion.PARTIDA_BLACKJACK, nueva);
        return EstadoBlackjack.de(nueva, cuenta.obtenerSaldo());
    }

    @PostMapping("/pedir")
    public EstadoBlackjack pedir(HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        PartidaBlackjack p = partida(sesion);
        blackjackService.pedir(p, cuenta);
        return guardar(sesion, p, cuenta);
    }

    @PostMapping("/plantarse")
    public EstadoBlackjack plantarse(HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        PartidaBlackjack p = partida(sesion);
        blackjackService.plantarse(p, cuenta);
        return guardar(sesion, p, cuenta);
    }

    @PostMapping("/doblar")
    public EstadoBlackjack doblar(HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        PartidaBlackjack p = partida(sesion);
        blackjackService.doblar(p, cuenta);
        return guardar(sesion, p, cuenta);
    }

    private PartidaBlackjack partida(HttpSession sesion) {
        return (PartidaBlackjack) sesion.getAttribute(Sesion.PARTIDA_BLACKJACK);
    }

    private EstadoBlackjack guardar(HttpSession sesion, PartidaBlackjack p, TAD_CuentaUsuario cuenta) {
        sesion.setAttribute(Sesion.PARTIDA_BLACKJACK, p);
        return EstadoBlackjack.de(p, cuenta.obtenerSaldo());
    }
}
