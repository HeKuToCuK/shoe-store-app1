package com.shoeshop.service;

import com.shoeshop.model.User;
import com.shoeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticateAndGetUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        throw new BadCredentialsException("Invalid password");
    }
    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authentication.getName()));
    }
    public User getCurrentSeller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        return userRepository.findByUsername(auth.getName())
                .filter(user -> "SELLER".equals(user.getRole().getRole_name())
                        || "ADMIN".equals(user.getRole().getRole_name()))
                .orElse(null);
    }

    public void loginAsSeller(String username, String password) {
        User seller = userRepository.findByUsername(username)
                .filter(user -> "SELLER".equals(user.getRole().getRole_name()))
                .orElseThrow(() -> new UsernameNotFoundException("Seller not found"));

        if (!passwordEncoder.matches(password, seller.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                seller.getUsername(),
                seller.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER"))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public boolean isSeller(User user) {
        return user != null && "SELLER".equalsIgnoreCase(user.getRole().getRole_name());
    }

    public boolean isCustomer(User user) {
        return user != null && "CUSTOMER".equalsIgnoreCase(user.getRole().getRole_name());
    }

}