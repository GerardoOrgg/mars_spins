package com.marsspins.servicio;

import com.marsspins.modelo.TAD_CuentaUsuario;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CuentaService {
    public static final double SALDO_INICIAL = 1000.0;

    private final Map<String, TAD_CuentaUsuario> cuentas = new ConcurrentHashMap<>();

    private final Map<String, String> contrasenas = new ConcurrentHashMap<>();
    private final AtomicInteger siguienteId = new AtomicInteger(1);

    public TAD_CuentaUsuario registrar(String usuario, String correo, String contrasena, String confirmar) {
        usuario = limpiar(usuario);
        correo = limpiar(correo);

        if (usuario.isEmpty()) {
            throw new MarsSpinsException("Escribe un nombre de usuario");
        }
        if (usuario.length() > 30) {
            throw new MarsSpinsException("El usuario debe tener maximo 30 caracteres");
        }
        if (!correo.isEmpty() && !correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new MarsSpinsException("El correo no es valido");
        }
        if (contrasena == null || contrasena.length() < 8) {
            throw new MarsSpinsException("La contrasena debe tener minimo 8 caracteres");
        }
        if (!contrasena.equals(confirmar)) {
            throw new MarsSpinsException("Las contrasenas no coinciden");
        }

        String clave = usuario.toLowerCase();
        TAD_CuentaUsuario nueva = new TAD_CuentaUsuario(siguienteId.getAndIncrement(), usuario, SALDO_INICIAL);
        if (cuentas.putIfAbsent(clave, nueva) != null) {
            throw new MarsSpinsException("Ese usuario ya existe");
        }
        contrasenas.put(clave, contrasena);
        return nueva;
    }

    public TAD_CuentaUsuario iniciarSesion(String usuario, String contrasena) {
        String clave = limpiar(usuario).toLowerCase();
        TAD_CuentaUsuario cuenta = cuentas.get(clave);
        if (cuenta == null || contrasena == null || !contrasena.equals(contrasenas.get(clave))) {
            throw new MarsSpinsException("Usuario o contrasena incorrectos");
        }
        return cuenta;
    }

    public TAD_CuentaUsuario buscar(String usuario) {
        if (usuario == null) {
            return null;
        }
        return cuentas.get(usuario.toLowerCase());
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
