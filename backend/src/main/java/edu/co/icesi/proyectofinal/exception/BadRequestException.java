package edu.co.icesi.proyectofinal.exception;

/** Los datos enviados no cumplen una regla de negocio (HTTP 400). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
