package edu.co.icesi.proyectofinal.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "routines")
@Data
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRoutine;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userRoutine;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(nullable = false)
    private LocalDate creationDate;
    @Column(nullable = false)
    private boolean predesigned;

    @OneToMany(mappedBy = "routine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineExercise> routineExercises;
}
