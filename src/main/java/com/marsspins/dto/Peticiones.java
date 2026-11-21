package com.marsspins.dto;

public final class Peticiones {
    private Peticiones() {}

    public record Login(String usuario, String contrasena) {}

    public record Registro(String usuario, String correo, String contrasena, String confirmar) {}

    public record Apuesta(Double monto) {}

    public record ApuestaRuleta(String color, Double monto) {}
}
