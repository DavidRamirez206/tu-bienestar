package edu.co.icesi.proyectofinal.exception;

/** La peticion es valida pero choca con el estado actual de los datos (HTTP 409). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
