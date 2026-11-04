package edu.co.icesi.proyectofinal.security;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private UserService userService;

    public CustomUserDetailService(UserService userService){this.userService = userService;}


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String institutionalEmail) throws UsernameNotFoundException {
        User user = userService.getUserByEmail(institutionalEmail);
        return new AppUser(user);
    }
}
