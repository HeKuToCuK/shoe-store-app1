package com.shoeshop.service;

import com.shoeshop.model.User;
import com.shoeshop.model.Role;
import com.shoeshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Инжектим PasswordEncoder напрямую

    private User testUser;

    @BeforeEach
    void setUp() {
        // Подготовка тестового пользователя
        testUser = new User();
        testUser.setUsername("test_user");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setRole(new Role(7L, "ADMIN", "Administrator"));
        testUser.setCreatedAt(LocalDateTime.now());
        userRepository.save(testUser);
    }

    @Test
    public void authenticateUser_Success() {
        User result = authService.authenticateAndGetUser("test_user", "password123");

        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
    }

    @Test
    public void authenticateUser_Fail_WrongPassword() {
        assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticateAndGetUser("test_user", "wrong_password")
        );
    }

    @Test
    public void authenticateUser_Fail_UserNotFound() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> authService.authenticateAndGetUser("non_existent_user", "password123")
        );
    }
}