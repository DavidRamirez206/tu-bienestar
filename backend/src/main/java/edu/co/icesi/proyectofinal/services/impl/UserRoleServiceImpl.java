package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.repository.UserRoleRepository;
import edu.co.icesi.proyectofinal.services.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserRole save(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public void deleteById(UserRoleId id) {
        userRoleRepository.deleteById(id);
    }

    @Override
    public UserRole getById(UserRoleId id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "UserRole with userId " + id.getUserId()
                                + " and roleId " + id.getRoleId() + " not found"));
    }

    @Override
    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    @Override
    public List<UserRole> getByUserId(Integer userId) {
        return userRoleRepository.findByUserIdUser(userId);
    }

    @Override
    public List<UserRole> getByRoleId(Long roleId) {
        return userRoleRepository.findByRoleId(roleId);
    }

    @Override
    public boolean existsByUserId(Integer userId) {
        return userRoleRepository.existsByUserIdUser(userId);
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        return userRoleRepository.existsByRoleId(roleId);
    }

    @Override
    public long countByRoleId(Long roleId) {
        return userRoleRepository.countByRoleId(roleId);
    }

    @Override
    public UserRole update(UserRoleId id, UserRole updated) {
        getById(id);
        updated.setId(id);
        return userRoleRepository.save(updated);
    }
}