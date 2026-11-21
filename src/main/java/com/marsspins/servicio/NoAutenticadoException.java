package com.marsspins.servicio;

public class NoAutenticadoException extends RuntimeException {
    public NoAutenticadoException() {
        super("Inicia sesion para continuar");
    }
}
