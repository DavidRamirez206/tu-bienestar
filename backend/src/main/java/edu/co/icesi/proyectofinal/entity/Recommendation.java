package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "recommendations")
@Data
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRecommendation;

    @ManyToOne
    @JoinColumn(name = "id_trainer")
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate dateCreated;
}
