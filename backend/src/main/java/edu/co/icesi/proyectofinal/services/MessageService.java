package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.api.v1.dto.ContactResponse;
import edu.co.icesi.proyectofinal.entity.Message;

import java.util.List;

/** HU16 · Mensajes y alertas. */
public interface MessageService {

    /** Mensajes enviados y recibidos por el usuario, del mas antiguo al mas reciente. */
    List<Message> findMine(String email);

    /**
     * Envia el mismo mensaje a cada destinatario. Solo se puede escribir a quien comparte una
     * asignacion con el remitente (entrenador y usuario asignado). Si algun destinatario no cumple,
     * no se envia ningun mensaje.
     */
    List<Message> send(String senderEmail, List<Integer> receiverIds, String content);

    /** Personas con las que el usuario puede conversar segun sus asignaciones. */
    List<ContactResponse> contacts(String email);
}
