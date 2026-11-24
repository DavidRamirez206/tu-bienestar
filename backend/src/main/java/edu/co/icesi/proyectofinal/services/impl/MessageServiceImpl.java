package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.api.v1.dto.ContactResponse;
import edu.co.icesi.proyectofinal.api.v1.dto.MessageResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.MessageMapper;
import edu.co.icesi.proyectofinal.config.WebSocketConfig;
import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.entity.Message;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import edu.co.icesi.proyectofinal.exception.BadRequestException;
import edu.co.icesi.proyectofinal.realtime.RealTimePublisher;
import edu.co.icesi.proyectofinal.repository.AssignmentRepository;
import edu.co.icesi.proyectofinal.repository.MessageRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.MessageService;
import edu.co.icesi.proyectofinal.services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AssignmentRepository assignmentRepository;
    private final NotificationService notificationService;
    private final MessageMapper messageMapper;
    private final RealTimePublisher publisher;

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository,
                              AssignmentRepository assignmentRepository, NotificationService notificationService,
                              MessageMapper messageMapper, RealTimePublisher publisher) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
        this.messageMapper = messageMapper;
        this.publisher = publisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> findMine(String email) {
        Integer userId = currentUser(email).getIdUser();
        return Stream.concat(messageRepository.findBySenderIdUser(userId).stream(),
                        messageRepository.findByReceiverIdUser(userId).stream())
                .distinct()
                .sorted(Comparator.comparing(Message::getSentAt).thenComparing(Message::getId))
                .toList();
    }

    @Override
    @Transactional
    public List<Message> send(String senderEmail, List<Integer> receiverIds, String content) {
        User sender = currentUser(senderEmail);
        String text = content == null ? "" : content.trim();
        if (text.isEmpty()) {
            throw new BadRequestException("El mensaje no puede estar vacío");
        }

        List<User> receivers = new ArrayList<>();
        for (Integer receiverId : new LinkedHashSet<>(receiverIds)) {
            if (receiverId.equals(sender.getIdUser())) {
                throw new BadRequestException("No puedes enviarte un mensaje a ti mismo");
            }
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new BadRequestException("El usuario " + receiverId + " no existe"));
            if (!shareAssignment(sender.getIdUser(), receiverId)) {
                throw new BadRequestException("Solo puedes enviar mensajes a tus usuarios asignados. "
                        + receiver.getFirstName() + " " + receiver.getLastName() + " no está asignado a ti");
            }
            receivers.add(receiver);
        }

        LocalDateTime now = LocalDateTime.now();
        List<Message> sent = new ArrayList<>();
        for (User receiver : receivers) {
            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setContent(text);
            message.setSentAt(now);
            Message saved = messageRepository.save(message);
            sent.add(saved);

            notificationService.notifyUser(receiver, sender, NotificationService.TYPE_NEW_MESSAGE,
                    "Nuevo mensaje de " + sender.getFirstName() + " " + sender.getLastName(),
                    saved.getId(), NotificationService.REF_MESSAGE);

            MessageResponse response = messageMapper.toResponse(saved);
            publisher.sendToUser(receiver.getInstitutionalEmail(), WebSocketConfig.MESSAGES_QUEUE, response);
            publisher.sendToUser(sender.getInstitutionalEmail(), WebSocketConfig.MESSAGES_QUEUE, response);
        }
        return sent;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactResponse> contacts(String email) {
        Integer userId = currentUser(email).getIdUser();
        Map<Integer, ContactResponse> contacts = new LinkedHashMap<>();
        for (Assignment assignment : assignmentRepository.findByTrainerAssignmentIdUser(userId)) {
            User trainee = assignment.getUserAssignment();
            contacts.putIfAbsent(trainee.getIdUser(), contact(trainee, "TRAINEE"));
        }
        for (Assignment assignment : assignmentRepository.findByUserAssignmentIdUser(userId)) {
            User trainer = assignment.getTrainerAssignment();
            contacts.putIfAbsent(trainer.getIdUser(), contact(trainer, "TRAINER"));
        }
        return contacts.values().stream()
                .sorted(Comparator.comparing(ContactResponse::firstName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(ContactResponse::lastName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private boolean shareAssignment(Integer senderId, Integer receiverId) {
        return assignmentRepository.existsById(assignmentId(senderId, receiverId))
                || assignmentRepository.existsById(assignmentId(receiverId, senderId));
    }

    private AssignmentId assignmentId(Integer trainerId, Integer userId) {
        AssignmentId id = new AssignmentId();
        id.setTrainerId(trainerId);
        id.setUserId(userId);
        return id;
    }

    private ContactResponse contact(User user, String relation) {
        return new ContactResponse(user.getIdUser(), user.getFirstName(), user.getLastName(),
                user.getInstitutionalEmail(), relation);
    }

    private User currentUser(String email) {
        return userRepository.findByInstitutionalEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
