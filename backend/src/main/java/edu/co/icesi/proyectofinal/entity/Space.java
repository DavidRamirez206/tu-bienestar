package edu.co.icesi.proyectofinal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "spaces")
@Data
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSpace;

    @Column(nullable = false)
    private String name;
    private Integer capacity;
    private String location;

}
