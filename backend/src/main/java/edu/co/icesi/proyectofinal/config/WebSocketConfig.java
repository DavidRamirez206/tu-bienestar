package edu.co.icesi.proyectofinal.config;

import edu.co.icesi.proyectofinal.security.WebSocketAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * T5 · Infraestructura WebSockets (STOMP).
 *
 * El punto de conexion vive en /api/ws para que el navegador envie la cookie de sesion
 * (su ruta es /api) en el handshake. Asi el JwtAuthenticationFilter autentica la conexion
 * igual que cualquier peticion REST.
 *
 * Destinos:
 *  - /user/queue/notifications: notificaciones del usuario conectado (HU15)
 *  - /user/queue/messages: mensajes enviados o recibidos por el usuario conectado (HU16)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final String ENDPOINT = "/api/ws";
    public static final String NOTIFICATIONS_QUEUE = "/queue/notifications";
    public static final String MESSAGES_QUEUE = "/queue/messages";

    private final WebSocketAuthenticationInterceptor authenticationInterceptor;
    private final List<String> allowedOrigins;

    public WebSocketConfig(WebSocketAuthenticationInterceptor authenticationInterceptor,
                           @Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(ENDPOINT).setAllowedOriginPatterns(allowedOrigins.toArray(String[]::new));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor);
    }
}
