package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    void deleteUserById(Integer userId);

    User getUserById(Integer idUser);

    List<User> getUsers();

    List<User> getUsersByFirstNameIgnoreCase(String firstName);

    List<User> getUserByLastName(String lastName);

    User getUserByEmail(String email);

    User getByFirstAndLastName(String firstName, String lastName);

    User updateUser(Integer userId, User updatedUser);

    void updateUserRoles(Integer userId, List<Long> roleIds);

    List<Role> getAllRoles();

    void updateUserProfile(String email, User updated);
}
