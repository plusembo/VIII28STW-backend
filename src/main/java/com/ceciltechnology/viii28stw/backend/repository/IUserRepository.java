package com.ceciltechnology.viii28stw.backend.repository;

import com.ceciltechnology.viii28stw.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    User findFirstByOrderByIdDesc();
}