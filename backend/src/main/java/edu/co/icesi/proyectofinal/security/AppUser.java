package edu.co.icesi.proyectofinal.security;

import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AppUser implements UserDetails {

    private User user;

    private List<GrantedAuthority> authorities;

    public AppUser(User user){
        this.user = user;
        authorities = new ArrayList<>();
        authorities.addAll(aux(user));
    }

    private List<SimpleGrantedAuthority> aux(User user){
        List<Role> userRoles = user.getUserRoles().stream()
                .map(userRole -> userRole.getRole())
                .toList();

        List<Policy> userPolicies = userRoles.stream()
                .flatMap(userRole -> userRole.getRolePolicies().stream())
                .map(rolePolicy -> rolePolicy.getPolicy())
                .toList();

        List<SimpleGrantedAuthority> permissionAuthorities = userPolicies.stream()
                .map(policy -> new SimpleGrantedAuthority(policy.getName()))
                .toList();

        List<SimpleGrantedAuthority> roleAuthorities = userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        List<SimpleGrantedAuthority> auths = new ArrayList<>();
        auths.addAll(permissionAuthorities);
        auths.addAll(roleAuthorities);

        return auths;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getInstitutionalEmail();
    }
}
