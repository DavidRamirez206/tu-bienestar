package edu.co.icesi.proyectofinal.entity;

import edu.co.icesi.proyectofinal.entity.keys.EnrollmentId;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @MapsId("activityId")
    @JoinColumn(name = "id_activity")
    private Activity activity;

    @Column(nullable = false)
    private LocalDate enrollmentDate;
}
