package com.shoeshop.repository;

import com.shoeshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.role.role_name = :roleName")
    List<User> findByRoleRole_name(@Param("roleName") String roleName);
    Optional<User> findByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.role.role_name = 'CUSTOMER'")
    List<User> findAllCustomers();
    @Query("SELECT u FROM User u WHERE u.role.role_name = :roleName AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<User> findByRoleAndSearchText(@Param("roleName") String roleName,
                                       @Param("searchText") String searchText);
}

