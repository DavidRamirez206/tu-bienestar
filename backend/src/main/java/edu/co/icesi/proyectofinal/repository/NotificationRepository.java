package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserTargetIdUser(Integer userId);

    List<Notification> findByUserTargetIdUserAndIsReadFalse(Integer userId);

    Optional<Notification> findTopByUserTargetIdUserOrderByDateSentDesc(Integer userId);

    long countByUserTargetIdUser(Integer userId);

}