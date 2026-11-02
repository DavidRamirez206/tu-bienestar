package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByUserIdUser(Integer userId);

    List<UserRole> findByRoleId(Long roleId);

    boolean existsByUserIdUser(Integer userId);

    boolean existsByRoleId(Long roleId);

    long countByRoleId(Long roleId);

}
