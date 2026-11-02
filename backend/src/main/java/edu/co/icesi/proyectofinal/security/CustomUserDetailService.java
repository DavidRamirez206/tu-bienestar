package edu.co.icesi.proyectofinal.security;

import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String institutionalEmail) throws UsernameNotFoundException {
        User user = userRepository.findByInstitutionalEmail(institutionalEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppUser(user);
    }
}
