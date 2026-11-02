package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotification;

    @ManyToOne
    @JoinColumn(name = "id_user_target")
    private User userTarget;

    @ManyToOne
    @JoinColumn(name = "id_user_source")
    private User userSource;

    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private Integer referenceId;
    @Column(nullable = false)
    private String referenceType;
    @Column(nullable = false)
    private LocalDateTime dateSent;
    @Column(nullable = false)
    private boolean isRead;
}
