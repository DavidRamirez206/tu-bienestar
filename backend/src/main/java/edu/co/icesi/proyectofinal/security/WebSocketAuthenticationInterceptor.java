package edu.co.icesi.proyectofinal.security;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida la sesion al abrir la conexion STOMP.
 *
 * El navegador ya llega autenticado: el handshake HTTP lleva la cookie de sesion y Spring Security
 * deja el usuario en la conexion. Otros clientes (por ejemplo Postman) pueden enviar el token en el
 * encabezado Authorization del frame CONNECT. Sin ninguno de los dos la conexion se rechaza.
 */
@Component
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthenticationInterceptor.class);

    private final JwtService jwtService;
    private final CustomUserDetailService userDetailsService;

    public WebSocketAuthenticationInterceptor(JwtService jwtService, CustomUserDetailService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        if (accessor.getUser() == null) {
            String token = bearerToken(accessor);
            if (token != null) {
                authenticate(accessor, token);
            }
        }

        if (accessor.getUser() == null) {
            throw new MessageDeliveryException("Unauthorized");
        }
        return message;
    }

    private String bearerToken(StompHeaderAccessor accessor) {
        List<String> values = accessor.getNativeHeader("Authorization");
        if (values == null || values.isEmpty()) {
            values = accessor.getNativeHeader("authorization");
        }
        if (values == null || values.isEmpty() || !values.get(0).startsWith("Bearer ")) {
            return null;
        }
        return values.get(0).substring(7);
    }

    private void authenticate(StompHeaderAccessor accessor, String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                accessor.setUser(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
            }
        } catch (RuntimeException ex) {
            log.debug("Token de WebSocket invalido: {}", ex.getMessage());
        }
    }
}
