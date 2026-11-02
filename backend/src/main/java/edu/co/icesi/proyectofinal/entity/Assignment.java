package edu.co.icesi.proyectofinal.entity;

import edu.co.icesi.proyectofinal.entity.keys.AssignmentId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Table(name = "assignments")
@Entity
@Data
public class Assignment {

    @EmbeddedId
    private AssignmentId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    private User userAssignment;

    @ManyToOne
    @MapsId("trainerId")
    @JoinColumn(name = "id_trainer")
    private User trainerAssignment;

    @Column(nullable = false)
    private LocalDate assignmentDate;
}
