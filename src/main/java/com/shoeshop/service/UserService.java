package com.shoeshop.service;

import com.shoeshop.model.Role;
import com.shoeshop.model.User;
import com.shoeshop.repository.RoleRepository;
import com.shoeshop.repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllEmployees() {
        return userRepository.findByRoleRole_name("EMPLOYEE");
    }
    public ObservableList<User> getAllEmployees2() {
        List<User> users = userRepository.findByRoleRole_name("EMPLOYEE"); // Или "ADMIN"
        return FXCollections.observableArrayList(users); // Преобразуем в ObservableList
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findCustomers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return userRepository.findByRoleRole_name("CUSTOMER");
        }
        return userRepository.findByRoleAndSearchText("CUSTOMER", searchText.toLowerCase());
    }
    public User saveCustomer(User customer) {
        if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("ФИО клиента не может быть пустым");
        }
        return userRepository.save(customer);
    }

    public User createUser(User user, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));
        user.setRole(role);
        return userRepository.save(user);
    }
    public User createCustomer(User customer) {
        if (customer.getUsername() == null || customer.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (customer.getPasswordHash() == null || customer.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        Role customerRole = roleRepository.findById(10L)
                .orElseThrow(() -> new RuntimeException("Роль покупателя не найдена"));

        customer.setRole(customerRole);
        customer.setCreatedAt(LocalDateTime.now());

        return userRepository.save(customer);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
