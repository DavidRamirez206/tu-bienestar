package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Space;

import java.util.List;

public interface SpaceService {
    List<Space> findAll();
    Space findById(Integer id);
    Space save(Space space);
    Space update(Integer id, Space space);
    void delete(Integer id);
}
