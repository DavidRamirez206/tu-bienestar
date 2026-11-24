package edu.co.icesi.proyectofinal.api.v1.dto;

/**
 * Persona con la que el usuario puede conversar.
 *
 * @param relation TRAINEE si es un usuario asignado al entrenador de la sesion,
 *                 TRAINER si es el entrenador asignado al usuario de la sesion
 */
public record ContactResponse(Integer idUser, String firstName, String lastName,
                              String institutionalEmail, String relation) {
}
