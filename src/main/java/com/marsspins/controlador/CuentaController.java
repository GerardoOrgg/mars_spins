package com.marsspins.controlador;

import com.marsspins.dto.CuentaDTO;
import com.marsspins.dto.Peticiones;
import com.marsspins.modelo.TAD_CuentaUsuario;
import com.marsspins.servicio.CuentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CuentaController {
    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @PostMapping("/registro")
    public CuentaDTO registrar(@RequestBody Peticiones.Registro datos, HttpSession sesion) {
        TAD_CuentaUsuario cuenta = cuentaService.registrar(
                datos.usuario(), datos.correo(), datos.contrasena(), datos.confirmar());
        sesion.setAttribute(Sesion.USUARIO, cuenta.getNombre());
        return CuentaDTO.de(cuenta);
    }

    @PostMapping("/login")
    public CuentaDTO login(@RequestBody Peticiones.Login datos, HttpSession sesion) {
        TAD_CuentaUsuario cuenta = cuentaService.iniciarSesion(datos.usuario(), datos.contrasena());
        sesion.setAttribute(Sesion.USUARIO, cuenta.getNombre());
        sesion.removeAttribute(Sesion.PARTIDA_BLACKJACK);
        return CuentaDTO.de(cuenta);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession sesion) {
        sesion.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cuenta/recargar")
    public CuentaDTO recargar(HttpSession sesion) {
        TAD_CuentaUsuario cuenta = Sesion.cuentaActual(sesion, cuentaService);
        cuenta.recargar(1000);
        return CuentaDTO.de(cuenta);
    }

    @GetMapping("/cuenta")
    public CuentaDTO cuenta(HttpSession sesion) {
        return CuentaDTO.de(Sesion.cuentaActual(sesion, cuentaService));
    }
}
