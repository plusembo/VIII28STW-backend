package com.ceciltechnology.viii28stw.backend.repository;

import com.ceciltechnology.viii28stw.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<User, String> {

    boolean existsByEmail(String email);
    User findByEmailAndSenha(String email, String senha);
    User findFirstByOrderByCodigoDesc();
}