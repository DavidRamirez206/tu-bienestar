package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findBySenderIdUser(Integer senderId);

    List<Message> findByReceiverIdUser(Integer receiverId);

    Optional<Message> findTopByReceiverIdUserOrderBySentAtDesc(Integer receiverId);
}
