package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByInstitutionalEmail(String email);

    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);

    boolean existsByInstitutionalEmail(String email);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    List<User> findByLastName(String lastName);

    Optional<User> findTopByOrderByIdUserDesc();

    Optional<User> getUserByIdUser(Integer userId);
}
