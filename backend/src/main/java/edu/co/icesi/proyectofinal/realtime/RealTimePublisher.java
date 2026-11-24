package edu.co.icesi.proyectofinal.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Envia datos por WebSocket a un usuario conectado.
 *
 * Si hay una transaccion abierta, el envio espera a que se confirme: asi nadie recibe en vivo algo
 * que luego no quedo guardado. Si el usuario no esta conectado el envio se pierde, pero el dato ya
 * esta en la base de datos y lo vera al volver a ingresar.
 */
@Component
public class RealTimePublisher {

    private static final Logger log = LoggerFactory.getLogger(RealTimePublisher.class);

    private final SimpMessagingTemplate messagingTemplate;

    public RealTimePublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /** @param username correo institucional del destinatario (es el nombre del usuario en la sesion) */
    public void sendToUser(String username, String destination, Object payload) {
        Runnable send = () -> {
            try {
                messagingTemplate.convertAndSendToUser(username, destination, payload);
            } catch (RuntimeException ex) {
                log.warn("No se pudo enviar {} a {} por WebSocket: {}", destination, username, ex.getMessage());
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send.run();
                }
            });
        } else {
            send.run();
        }
    }
}
