package com.marsspins.controlador;

import com.marsspins.servicio.MarsSpinsException;
import com.marsspins.servicio.NoAutenticadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ManejadorErrores {
    @ExceptionHandler(MarsSpinsException.class)
    public ResponseEntity<Map<String, String>> reglas(MarsSpinsException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(NoAutenticadoException.class)
    public ResponseEntity<Map<String, String>> sinSesion(NoAutenticadoException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Datos invalidos"));
    }
}
