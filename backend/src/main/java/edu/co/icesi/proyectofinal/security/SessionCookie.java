package edu.co.icesi.proyectofinal.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Cookie que guarda el token de la sesion. HttpOnly impide leerla desde JavaScript y SameSite=Strict
 * evita que otro sitio la use para hacer peticiones en nombre del usuario.
 */
@Component
public class SessionCookie {

    public static final String NAME = "mb_session";

    @Value("${app.security.expirationMinutes}")
    private long expirationMinutes;

    // Debe activarse cuando la aplicacion se sirva por HTTPS.
    @Value("${app.security.cookie-secure:false}")
    private boolean secure;

    public String create(String token) {
        return build(token, Duration.ofMinutes(expirationMinutes));
    }

    public String clear() {
        return build("", Duration.ZERO);
    }

    private String build(String value, Duration maxAge) {
        return ResponseCookie.from(NAME, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/api")
                .maxAge(maxAge)
                .build()
                .toString();
    }
}
