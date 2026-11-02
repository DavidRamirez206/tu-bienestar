package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProgress;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User userProgress;

    @ManyToOne
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_routine")
    private Routine routine;

    @Column(nullable = false)
    private LocalDateTime dateLogged;
    private Integer reps;
    private Integer durationMin;
    private Integer effortLevel;
    private Integer setNumber;
    private Integer weightKg;
}
