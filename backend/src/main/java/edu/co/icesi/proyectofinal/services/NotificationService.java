package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Notification;
import edu.co.icesi.proyectofinal.entity.User;

import java.util.List;

/** HU15 · Notificaciones en tiempo real. */
public interface NotificationService {

    String TYPE_NEW_ACTIVITY = "NEW_ACTIVITY";
    String TYPE_NEW_SPACE = "NEW_SPACE";
    String TYPE_NEW_MESSAGE = "NEW_MESSAGE";
    String TYPE_NEW_RECOMMENDATION = "NEW_RECOMMENDATION";

    String REF_ACTIVITY = "ACTIVITY";
    String REF_SPACE = "SPACE";
    String REF_MESSAGE = "MESSAGE";
    String REF_RECOMMENDATION = "RECOMMENDATION";

    /** Notificaciones del usuario, de la mas reciente a la mas antigua. */
    List<Notification> findMine(String email, boolean unreadOnly);

    Notification markAsRead(Integer id, String email);

    int markAllAsRead(String email);

    void delete(Integer id, String email);

    /** Guarda una notificacion para cada usuario y la envia en vivo a los que estan conectados. */
    void notifyAllUsers(String type, String message, Integer referenceId, String referenceType);

    /** Guarda una notificacion para un usuario y se la envia en vivo si esta conectado. */
    Notification notifyUser(User target, User source, String type, String message,
                            Integer referenceId, String referenceType);
}
