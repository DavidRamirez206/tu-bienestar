package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "exercises")
@Data
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idExercise;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExercise> exercisesRoutines;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;
    private String description;
    private Integer durationMin;
    private String difficulty;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userExercise;

    @Column(nullable = false)
    private boolean predefined;


}
