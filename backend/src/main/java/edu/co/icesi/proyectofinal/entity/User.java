package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(icesi\\.edu\\.co|u\\.icesi\\.edu\\.co)$")
    private String institutionalEmail;

    @Column(nullable = false)
    private String password;

    private Integer age;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "userProgress", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progresses;

    @OneToMany(mappedBy = "userRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Routine> routines;

    //assignments
    @OneToMany(mappedBy = "userAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments;

    @OneToMany(mappedBy = "trainerAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> trainerAssignments;

    //recommendations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> trainerRecommendations;

    //messages
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> receivedMessages;

    //notifications

    @OneToMany(mappedBy = "userTarget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> targetNotifications;

    @OneToMany(mappedBy = "userSource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> sourceNotifications;

    @OneToMany(mappedBy = "userExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List <Exercise> userExercises;

}
