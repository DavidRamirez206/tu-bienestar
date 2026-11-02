package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.RoleRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final String DEFAULT_ROLE = "TRAINEE";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Transactional
    public User saveUser(User user){
        if (userRepository.existsByInstitutionalEmail(user.getInstitutionalEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        if (savedUser.getUserRoles() == null || savedUser.getUserRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(DEFAULT_ROLE)
                    .orElseThrow(() -> new IllegalStateException(
                            "Default role " + DEFAULT_ROLE + " is not configured"));

            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(savedUser.getIdUser());
            userRoleId.setRoleId(defaultRole.getId());

            UserRole userRole = new UserRole();
            userRole.setId(userRoleId);
            userRole.setUser(savedUser);
            userRole.setRole(defaultRole);

            savedUser.setUserRoles(new ArrayList<>(List.of(userRole)));
            savedUser = userRepository.save(savedUser);
        }

        return savedUser;
    }

    public void deleteUserById(Integer userId){
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User with id "+ userId +" not found");
        }

        userRepository.deleteById(userId);
    }

    public User getUserById(Integer idUser){
        return userRepository.getUserByIdUser(idUser)
                .orElseThrow(() ->
                        new RuntimeException("User with id "+ idUser
                        + " not found"));
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public List<User> getUsersByFirstNameIgnoreCase(String firstName){
        return userRepository.findByFirstNameContainingIgnoreCase(firstName);
    }

    public List<User> getUserByLastName(String lastName){
        return userRepository.findByLastName(lastName);
    }

    public User getUserByEmail(String email){
        return userRepository.findByInstitutionalEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User with email " + email
                                + " not found"));
    }

    public User getByFirstAndLastName(String firstName, String lastName){

        return userRepository.findByFirstNameAndLastName(firstName,lastName)
                .orElseThrow(() ->
                        new RuntimeException("User with first name " + firstName
                        + " and last name " + lastName + " not found"));
    }

    public User updateUser(Integer userId, User updatedUser) {
        User existing = userRepository.getUserByIdUser(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " not found"));

        if (updatedUser.getFirstName() != null) existing.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) existing.setLastName(updatedUser.getLastName());
        if (updatedUser.getInstitutionalEmail() != null) existing.setInstitutionalEmail(updatedUser.getInstitutionalEmail());
        if (updatedUser.getAge() != null) existing.setAge(updatedUser.getAge());

        return userRepository.save(existing);
    }

    public void updateUserRoles(Integer userId, List<Long> roleIds){

        User user = userRepository.findById(userId).orElseThrow();

        user.getUserRoles().clear();

        if(roleIds != null){
            for(Long roleId : roleIds){

                Role role = roleRepository.findById(roleId).orElseThrow();

                UserRoleId id = new UserRoleId();
                id.setRoleId(role.getId());
                id.setUserId(user.getIdUser());

                UserRole ur = new UserRole();
                ur.setId(id);
                ur.setUser(user);
                ur.setRole(role);

                user.getUserRoles().add(ur);
            }
        }

        userRepository.save(user);
    }

    @Override
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    @Override
    public void updateUserProfile(String email, User updated){

        User user = getUserByEmail(email);

        user.setFirstName(updated.getFirstName());
        user.setLastName(updated.getLastName());
        user.setAge(updated.getAge());

        userRepository.save(user);
    }

}
