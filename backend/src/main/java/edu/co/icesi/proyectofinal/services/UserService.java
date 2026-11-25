package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);

    User saveUserWithRole(User user, String roleName);

    List<User> getUsersByRole(String roleName);

    void deleteUserById(Integer userId);

    User getUserById(Integer idUser);

    List<User> getUsers();

    List<User> getUsersByFirstNameIgnoreCase(String firstName);

    List<User> getUserByLastName(String lastName);

    User getUserByEmail(String email);

    User getByFirstAndLastName(String firstName, String lastName);

    User updateUser(Integer userId, User updatedUser);

    User setUserActive(Integer userId, boolean active);

    void updateUserRoles(Integer userId, List<Long> roleIds);

    List<Role> getAllRoles();

    void updateUserProfile(String email, User updated);
}
