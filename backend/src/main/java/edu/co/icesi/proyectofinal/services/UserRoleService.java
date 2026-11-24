package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;

import java.util.List;

public interface UserRoleService {

    UserRole save(UserRole userRole);

    void deleteById(UserRoleId id);

    UserRole getById(UserRoleId id);

    List<UserRole> findAll();

    List<UserRole> getByUserId(Integer userId);

    List<UserRole> getByRoleId(Long roleId);

    boolean existsByUserId(Integer userId);

    boolean existsByRoleId(Long roleId);

    long countByRoleId(Long roleId);

    UserRole update(UserRoleId id, UserRole updated);
}