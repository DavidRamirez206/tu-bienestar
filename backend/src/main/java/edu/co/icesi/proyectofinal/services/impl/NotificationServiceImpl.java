package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.api.v1.mapper.NotificationMapper;
import edu.co.icesi.proyectofinal.config.WebSocketConfig;
import edu.co.icesi.proyectofinal.entity.Notification;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.realtime.RealTimePublisher;
import edu.co.icesi.proyectofinal.repository.NotificationRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final RealTimePublisher publisher;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository,
                                   NotificationMapper notificationMapper, RealTimePublisher publisher) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
        this.publisher = publisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findMine(String email, boolean unreadOnly) {
        Integer userId = currentUser(email).getIdUser();
        List<Notification> notifications = unreadOnly
                ? notificationRepository.findByUserTargetIdUserAndIsReadFalse(userId)
                : notificationRepository.findByUserTargetIdUser(userId);
        return notifications.stream()
                .sorted(Comparator.comparing(Notification::getDateSent).reversed())
                .toList();
    }

    @Override
    @Transactional
    public Notification markAsRead(Integer id, String email) {
        Notification notification = findOwn(id, email);
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public int markAllAsRead(String email) {
        List<Notification> unread = notificationRepository
                .findByUserTargetIdUserAndIsReadFalse(currentUser(email).getIdUser());
        unread.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unread);
        return unread.size();
    }

    @Override
    @Transactional
    public void delete(Integer id, String email) {
        notificationRepository.delete(findOwn(id, email));
    }

    @Override
    @Transactional
    public void notifyAllUsers(String type, String message, Integer referenceId, String referenceType) {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = userRepository.findAll().stream()
                .map(user -> build(user, null, type, message, referenceId, referenceType, now))
                .toList();
        notificationRepository.saveAll(notifications).forEach(this::publish);
    }

    @Override
    @Transactional
    public Notification notifyUser(User target, User source, String type, String message,
                                   Integer referenceId, String referenceType) {
        Notification saved = notificationRepository.save(
                build(target, source, type, message, referenceId, referenceType, LocalDateTime.now()));
        publish(saved);
        return saved;
    }

    private Notification build(User target, User source, String type, String message,
                               Integer referenceId, String referenceType, LocalDateTime date) {
        Notification notification = new Notification();
        notification.setUserTarget(target);
        notification.setUserSource(source);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setDateSent(date);
        notification.setRead(false);
        return notification;
    }

    private void publish(Notification notification) {
        publisher.sendToUser(notification.getUserTarget().getInstitutionalEmail(),
                WebSocketConfig.NOTIFICATIONS_QUEUE, notificationMapper.toResponse(notification));
    }

    private User currentUser(String email) {
        return userRepository.findByInstitutionalEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Una notificacion de otro usuario se trata como inexistente para no revelar que existe.
    private Notification findOwn(Integer id, String email) {
        return notificationRepository.findById(id)
                .filter(n -> n.getUserTarget() != null && email.equals(n.getUserTarget().getInstitutionalEmail()))
                .orElseThrow(() -> new RuntimeException("Notification with id " + id + " not found"));
    }
}
