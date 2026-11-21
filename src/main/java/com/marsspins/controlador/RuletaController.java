package com.marsspins.controlador;

import com.marsspins.dto.Peticiones;
import com.marsspins.dto.ResultadoRuleta;
import com.marsspins.modelo.TAD_CuentaUsuario;
import com.marsspins.servicio.CuentaService;
import com.marsspins.servicio.RuletaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ruleta")
public class RuletaController {
    private final RuletaService ruletaService;
    private final CuentaService cuentaService;

    public RuletaController(RuletaService ruletaService, CuentaService cuentaService) {
        this.ruletaService = ruletaService;
        this.cuentaService = cuentaService;
    }

    @GetMapping("/casillas")
    public String[] casillas() {
        return ruletaService.getCasillas();
    }

    @PostMapping("/girar")
    public ResultadoRuleta girar(@RequestBody Peticiones.ApuestaRuleta datos, HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        return ruletaService.girar(cuenta, datos.color(), datos.monto());
    }
}
